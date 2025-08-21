package com.lm.order.exceptionHandler;

//@Component
//public class MyBlockExceptionHandler implements BlockExceptionHandler {
//    private ObjectMapper objectMapper = new ObjectMapper();
//    @Override
//    public void handle(HttpServletRequest request, HttpServletResponse response,
//                       String resourceName, BlockException e) throws Exception {
//        response.setStatus(429); //too many requests
//        response.setContentType("application/json;charset=utf-8");
//
//        PrintWriter writer = response.getWriter();
//
//
//        R error = R.error(500, resourceName + " 被Sentinel限制了，原因：" + e.getClass());
//
//        String json = objectMapper.writeValueAsString(error);
//        writer.write(json);
//
//        writer.flush();
//        writer.close();
//    }
//}
