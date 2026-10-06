

package org.example.handler;

import org.example.util.myDbUtil;

import java.util.Map;

public class GetMyLamdaHandler {

    private final myDbUtil dbUtil = new myDbUtil();

    public Object handleRequest(Map<String, Object> input) {

        String id = (String) input.get("id");

        return dbUtil.getLambda(id);
    }
}
