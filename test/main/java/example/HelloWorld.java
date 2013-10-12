/*
 * $Id: HelloWorld.java 471756 2006-11-06 15:01:43Z husted $
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package example;


import java.util.ArrayList;

import org.zz.qstruts2.action.QAction;
import org.zz.qstruts2.annotations.ActionController;
import org.zz.qstruts2.annotations.RequestMapping;
import org.zz.qstruts2.dispatcher.ng.result.VelocityResult;

import com.opensymphony.xwork2.Result;

/**
 * <code>Set welcome message.</code>
 */
@ActionController(value="hello")
public class HelloWorld extends QAction {

	@RequestMapping(value="index")
    public Result index() throws Exception {

    	setHttpAttribute("list", getNames());
        return new VelocityResult("/example/example.vm");
    }

    private ArrayList<String> getNames()
    {
        ArrayList<String> list = new ArrayList<String>();

        list.add("ArrayList element 1");
        list.add("ArrayList element 2");
        list.add("ArrayList element 3");
        list.add("ArrayList element 4");

        return list;
    }
}
