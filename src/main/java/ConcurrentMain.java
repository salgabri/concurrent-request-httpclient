import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ConcurrentMain {

    private static final String URL = "https://jsonplaceholder.typicode.com/posts/";
    private static final HttpClient client = HttpClient.newHttpClient();

    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {

        long startTime = System.currentTimeMillis();

        List<Result> results = new LinkedList<>();
        List<CompletableFuture<?>> futures = new LinkedList<>();
        for(int i = 1; i < 99; i++) {
            Result result = new Result();
            result.setNum(i);
            results.add(result);

            HttpRequest request = HttpRequest.newBuilder(new URI(URL + i)).build();
            CompletableFuture<?> future = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                        .thenApply(HttpResponse::body)
                        .exceptionally(e -> "Error: " + e.getMessage())
                        .thenAccept(body -> {
                            System.out.println(body);
                            result.setJson(body);
                        });
            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).join();

        System.out.print("time " + (System.currentTimeMillis() - startTime));

        /*List<String> results = new LinkedList<>();
        long startTime = System.currentTimeMillis();
        List<HttpRequest> requests = IntStream.range(1, 99)
                .mapToObj(num -> URL + num)
                .map(URI::create)
                .map(uri -> HttpRequest.newBuilder(uri).build())
                .collect(Collectors.toList());

        CompletableFuture<?>[] responses = requests.stream()
                .map(request -> client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                        .thenApply(HttpResponse::body)
                        .exceptionally(e -> "Error: " + e.getMessage())
                        .thenAccept(result -> {
                            System.out.println(result);
                            results.add(result);
                        }))
                .toArray(CompletableFuture<?>[]::new);

        CompletableFuture.allOf(responses).join();

        System.out.print("time " + (System.currentTimeMillis() - startTime));*/

        /*long startTime = System.currentTimeMillis();

        List<Result> results = new LinkedList<>();

        for(int i = 1; i < 99; i++) {
            Result result = new Result();
            result.setNum(i);
            results.add(result);

            HttpRequest request = HttpRequest.newBuilder(new URI(URL + i)).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();
            result.setJson(body);
        }


        System.out.print("time " + (System.currentTimeMillis() - startTime));*/

    }
}

