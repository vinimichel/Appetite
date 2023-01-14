
public class api_call_test {

    public static void main(String[] args) {
        OkHttpClient client = new OkHttpClient().newBuilder()
  .build();
MediaType mediaType = MediaType.parse("text/plain");
RequestBody body = RequestBody.create(mediaType, "");
Request request = new Request.Builder()
  .url("http://localhost:3000/reservations")
  .method("GET", body)
  .build();
Response response = client.newCall(request).execute();
    }
}
