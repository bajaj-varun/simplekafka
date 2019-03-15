import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.*;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import org.bson.Document;

import java.util.Arrays;

public class PersonalCustMaint {
    public static void main(String[] args) {
        /*final  String COL1 = "XXX";
        final  String COL2 = "XXX";*/

        final  String COL1 = "XXX";
        final  String COL2 = "XXX";

        MongoClientURI mongoClientURI = new MongoClientURI("mongodb://XXXX:XXXX@XXX/");
        final MongoClient mongoClient = new MongoClient(mongoClientURI);
        MongoDatabase db = mongoClient.getDatabase("testdb");
        MongoCollection<Document> collection = db.getCollection(COL1);

        /*db.XXX.aggregate( [
            { $group : { _id : "$XXX", rating_count: { $sum: 1 } } },
            { $out : "XXX" }
        ])*/

         Block<ChangeStreamDocument<Document>> printBlock = new Block<ChangeStreamDocument<Document>>() {
            public void apply(final ChangeStreamDocument<Document> changeStreamDocument) {
                System.out.println(" MyService:::"+changeStreamDocument.getFullDocument());

                AggregateIterable output = collection.aggregate(Arrays.asList(
                        Aggregates.group("XXX", Accumulators.sum("XXX",1)),
                        Aggregates.out(COL2)
                ));
                output.batchSize(1000).allowDiskUse(true);

                /*MongoCursor<Document> iterator = output.iterator();
                while (iterator.hasNext()) {
                    Document next = iterator.next();
                    System.out.println("updated Record => "+next.toString());
                }*/
            }
        };

        ChangeStreamIterable<Document> changes = collection.watch();
        changes.forEach(printBlock);

        MongoCollection col2 = db.getCollection(COL2);
        col2.find().iterator().forEachRemaining(v -> System.out.println(v));
    }
}
