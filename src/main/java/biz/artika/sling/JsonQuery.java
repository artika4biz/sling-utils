/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */



package biz.artika.sling;

import java.io.IOException;
import javax.jcr.*;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
        immediate=true,
        service = javax.servlet.Servlet.class,
        property = {
                "service.description:String=Json Query Servlet",
                "service.vendor:String=Yuri Simione - https://linkedin.com/in/yurisimione",
                "sling.servlet.paths:String=/v1/jcr-query"
        })

/**
 * A simple servlet that provides results of a SQL-2 query in Json format, with pagination.
 * Servlet arguments:
 * sql: the query to be executed
 * offset: starting point of the result set (see javax.jcr.query.Query documentation)
 * limit: maximum number of nodes returned (see javax.jcr.query.Query documentation)
 * eaxample: http://localhost:8080/v1/jcr-query?offset=0&limit=100&sql=select * from [oak:Unstructured] as n where isdescendantnode(n,'/data/archive/')
 * @author Yuri Simione - https://linkedin.com/in/yurisimione
 */
public class JsonQuery extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;

    private final Logger log = LoggerFactory.getLogger(getClass());



    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Session session = request.getResourceResolver().adaptTo(Session.class);
        if(session.getUserID().equals("anonymous") || session.getUserID() == null)  {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String queryText = request.getParameter("sql");
        if ( queryText == null || queryText.isEmpty() ) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Missing mandatory 'sql' parameter");
            return;
        }

        log.debug("sql parameter is: " + queryText);
        int offset = 0;
        try {
            offset = Integer.parseInt(request.getParameter("offset"));
        } catch (Exception e) {
                log.debug("Offset parameter not valid or not provided. Use default value 0");
        }

        int limit = 10;
        try {
            limit = Integer.parseInt(request.getParameter("limit"));
        } catch (Exception e) {
            log.debug("Limit parameter not valid or not provided. Use default value 10");
        }

        try {
            StringBuffer sb = new StringBuffer("{ \"results\":[");
            int lenPrefix = sb.length();

            Query query = session.getWorkspace().getQueryManager().createQuery(queryText, Query.JCR_SQL2);
            query.setOffset(offset);
            query.setLimit(limit);
            QueryResult result  = query.execute();
            NodeIterator iterator = result.getNodes();
                while( iterator.hasNext() ) {
                Node node = iterator.nextNode();
                sb.append("{\"node\": \"").append(node.getPath()).append("\"},");

            }
            // remove last comma symbol, if needed
            if( sb.length() > lenPrefix)
                sb.deleteCharAt( sb.length() - 1 );
            sb.append("]}");
            response.getWriter().println(sb);
        } catch (RepositoryException e) {
            throw new ServletException(e);
        }

    }

}