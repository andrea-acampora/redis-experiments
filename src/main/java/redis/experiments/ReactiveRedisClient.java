package redis.experiments;

import io.lettuce.core.RedisClient;
import io.lettuce.core.StreamMessage;
import io.lettuce.core.XReadArgs;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.reactive.RedisReactiveCommands;

import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReactiveRedisClient {

    private final String clientId;
    private final RedisClient redisClient;
    private StatefulRedisConnection<String, String> connection;
    private RedisReactiveCommands<String, String> commands;
    private Map<String, Disposable> subscriptions;

    public ReactiveRedisClient(String clientId, final String connectionString) {
        this.clientId = clientId;
        this.subscriptions = new HashMap<>();
        if (connectionString == null)
            throw new IllegalArgumentException("Invalid Connection String");
        try {
            this.redisClient = ReactiveRedisClient.create(connectionString);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid Connection String");
        }
    }

    public void connect() {
        try {
            this.connection = redisClient.connect();
            this.commands = this.connection.reactive();
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to connect to Redis instance!");
        }
    }

    public void subscribe(String topic) {
        Flux<StreamMessage<String, String>> flux = this.commands.xread(XReadArgs.StreamOffset.latest(topic));
        Disposable subscription = flux.subscribe(message -> System.out.println("Received message: " + message));
        this.subscriptions.put(topic, subscription);
    }

    public void unSubscribe(String topic) {
        if (this.subscriptions.containsKey(topic)) {
            this.subscriptions.remove(topic).dispose();
        }
    }

    public void subscribe(String topic, String eventType) {}

    public void subscribe(String topic, List<String> eventTypeList) {}

    public void unSubscribe(String topic, String eventType) {}

    public void unSubscribe(String topic, List<String> eventTypeList) {}

    public void disconnect() {
        this.connection.close();
    }
    public void publishMessage(String topic, String message) {
        this.commands.xadd(topic, message);
    }
}
