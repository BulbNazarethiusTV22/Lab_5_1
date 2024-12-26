import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(3);

        CompletableFuture<String> source1 = CompletableFuture.supplyAsync(() -> fetchData("Source 1"), executor);
        CompletableFuture<String> source2 = CompletableFuture.supplyAsync(() -> fetchData("Source 2"), executor);
        CompletableFuture<String> source3 = CompletableFuture.supplyAsync(() -> fetchData("Source 3"), executor);

        CompletableFuture<String> composedTask = source1.thenCompose(data1 -> CompletableFuture.supplyAsync(() -> 
                "Composed with " + data1));

        CompletableFuture<String> combinedTask = source1.thenCombine(source2, (data1, data2) ->
                "Combined: " + data1 + " and " + data2);

        CompletableFuture<Void> allOfTask = CompletableFuture.allOf(source1, source2, source3);
        allOfTask.thenRun(() -> {
            try {
                String result1 = source1.get();
                String result2 = source2.get();
                String result3 = source3.get();
                System.out.println("All data fetched:");
                System.out.println(result1);
                System.out.println(result2);
                System.out.println(result3);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });

        CompletableFuture<Object> anyOfTask = CompletableFuture.anyOf(source1, source2, source3);
        anyOfTask.thenAccept(result ->
                System.out.println("First completed: " + result));

        allOfTask.join();

        executor.shutdown();
    }

    private static String fetchData(String source) {
        try {
            Thread.sleep((long) (Math.random() * 2000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "Data from " + source;
    }
}
