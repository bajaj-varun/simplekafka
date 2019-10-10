//package connectors;
//
//import org.apache.kafka.connect.connector.Task;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.Map;
//
//public class FileStreamSourceTask implements Task {
//    private String filename;
//    private InputStream stream;
//    private String topic;
//
//    @Override
//    public String version() {
//        return null;
//    }
//
//    @Override
//    public void start(Map<String, String> map) {
//        filename = map.get(FileStreamSourceConnector.FILE_CONFIG);
//        stream = openOrThrowError(filename);
//        topic = map.get(FileStreamSourceConnector.TOPIC_CONFIG);
//    }
//
//    @Override
//    public void stop() {
//        try {
//            stream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}
