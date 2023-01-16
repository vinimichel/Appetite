import java.io.*;
import okhttp3.*;

public class api_call_test {

    public static void getRequest(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            System.out.println(responseBody);
        }
    }

    public static void postRequest(RequestBody postBody, String postUrl) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url(postUrl)
                .method("POST",postBody)
                .build();
        Response response = client.newCall(request).execute();
        System.out.println(response.body().string());

    }
    public static void main(String[] args) throws IOException {

        String getUrl = "http://localhost:3000/users/63b347bfa3259f7f3b5b8f0c";
        //getRequest(getUrl);
        //post Request
        String postUrl = "http://localhost:3000/auth/user/register";
        MediaType mediaType = MediaType.parse("text/plain");
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, "{\n" +
                "    \"firstname\" : \"Mohammed\",\n" +
                "    \"lastname\" : \"Dahi\",\n" +
                "    \"email\" : \"mdahi@gmail.com\",\n" +
                "    \"password\" : \"kjefwhjefksb122334@@\",\n" +
                "    \"address\" : \"Hamburgerstrasse 13\",\n" +
                "    \"PLZ\" : \"36039\"\n" +
                "}");
        postRequest(body, postUrl);
    }
}
