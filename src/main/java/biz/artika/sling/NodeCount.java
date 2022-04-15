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

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component(
        immediate=true,
        service = javax.servlet.Servlet.class,
        property = {
                "service.description:String=Json Query Servlet",
                "service.vendor:String=Yuri Simione - https://linkedin.com/in/yurisimione",
                "sling.servlet.paths:String=/v1/count"
        })

/*
  A simple servlet that provides count of the the SQL-2 queryA
  rguments:
  sql: the query to be executed
  example: http://localhost:8080/v1/count?sql=select * from [oak:Unstructured] as n where isdescendantnode(n,'/data/archive/')
  @author Yuri Simione - https://linkedin.com/in/yurisimione
 */
public class NodeCount extends SlingSafeMethodsServlet {

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
        long startTime = System.currentTimeMillis();
        log.debug("sql parameter is: " + queryText);
        long count = 0;
        try {
            Query query = session.getWorkspace().getQueryManager().createQuery(queryText, Query.JCR_SQL2);
            QueryResult result  = query.execute();

            NodeIterator iterator = result.getNodes();
            while( iterator.hasNext() ) {
                Node node = iterator.nextNode();
                ++count;
            }
            long finishTime = System.currentTimeMillis();
            long duration = finishTime-startTime;
            response.getWriter().println("{\"count\":" + count +
                    ",\"execution time (ms)\":" + duration + "}");
        } catch (RepositoryException e) {
            throw new ServletException(e);
        }

    }

}