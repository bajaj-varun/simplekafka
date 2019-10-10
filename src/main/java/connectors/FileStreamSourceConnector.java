//package connectors;
//
//import org.apache.kafka.common.config.ConfigDef;
//import org.apache.kafka.connect.connector.Task;
//import org.apache.kafka.connect.source.SourceConnector;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class FileStreamSourceConnector extends SourceConnector{
//    private String filename;
//    private String topic;
//
//    @Override
//    public void start(Map<String, String> map) {
//        filename = map.get(FILE_CONFIG);
//        topic = map.get(TOPIC_CONFIG);
//    }
//
//    @Override
//    public Class<? extends Task> taskClass() {
//        return FileStreamSourceTask.class;
//    }
//
//    @Override
//    public List<Map<String, String>> taskConfigs(int i) {
//        ArrayList<Map<String, String>> configs = new ArrayList<>();
//        // Only one input partition makes sense.
//        Map<String, String> config = new HashMap<>();
//        if (filename != null)
//            config.put(FILE_CONFIG, filename);
//        config.put(TOPIC_CONFIG, topic);
//        configs.add(config);
//        return configs;
//    }
//
//    @Override
//    public void stop() {
//
//    }
//
//    @Override
//    public ConfigDef config() {
//        return null;
//    }
//
//    @Override
//    public String version() {
//        return null;
//    }
//}
