# simplekafka
Simple Kafka example, will be creating example of Joining 2 streams and storing values to DB.

### Example code 
[Stream join](src/main/java/com/example/models)

### Generate sample data in "Users" topic
`$ ksql-datagen quickstart=users topic=users maxInterval=1000 schemaRegistryUrl="http://localhost:8081"  format=avro`

### Validate values on console
` ksql-datagen topic=users maxInterval=1000 schemaRegistryUrl="http://localhost:8081"  format=avro schema=users.avro key=userid`
```
User_20 --> ([ 1516490143893 | 'User_20' | 'Region_1' | 'MALE' ]) ts:1546493751045
User_10 --> ([ 1508919732379 | 'User_10' | 'Region_4' | 'OTHER' ]) ts:1546493751299
User_42 --> ([ 1511263168089 | 'User_42' | 'Region_5' | 'OTHER' ]) ts:1546493752220
```

### Validate schema on schema-registry
    $ curl "http://localhost:8081/subjects"
    ["users-value"]


### Get schema of topic values
    $ curl "http://localhost:8081/subjects/users-value/versions"
    [1,2,3]
    $curl "http://localhost:8081/subjects/users-value/versions/3"`

    {"subject":"users-value","version":3,"id":7,"schema":"{\"type\":\"record\",\"name\":\"KsqlDataSourceSchema\",\"namespace\":\"io.confluent.ksql.avro_schemas\",\"fields\":[{\"name\":\"userid\",\"type\":[\"null\",\"string\"],\"default\":null}]}"}


### Format schema to use in downstream code
```
{
	"type": "record",
	"name": "KsqlDataSourceSchema",
	"namespace": "io.confluent.ksql.avro_schemas",
	"fields": [{
		"name": "registertime",
		"type": ["null", "long"],
		"default": null
	}, {
		"name": "userid",
		"type": ["null", "string"],
		"default": null
	}, {
		"name": "regionid",
		"type": ["null", "string"],
		"default": null
	}, {
		"name": "gender",
		"type": ["null", "string"],
		"default": null
	}]
}
```

### Important : Point to note here to keep namespace-io.confluent.ksql.avro_schemas, and class-"KsqlDataSourceSchema". More details can be found at - https://github.com/confluentinc/ksql/pull/1863

Test first stream - SimpleStreamJoin.java
```
[KSTREAM-SOURCE-0000000000]: User_32, {"registertime": 1516026799546, "userid": "User_32", "regionid": "Region_8", "gender": "FEMALE"}
[KSTREAM-SOURCE-0000000000]: User_32, {"registertime": 1505517042726, "userid": "User_32", "regionid": "Region_7", "gender": "MALE"}
[KSTREAM-SOURCE-0000000000]: User_12, {"registertime": 1518486459580, "userid": "User_12", "regionid": "Region_3", "gender": "OTHER"}
[KSTREAM-SOURCE-0000000000]: User_72, {"registertime": 1487993659517, "userid": "User_72", "regionid": "Region_1", "gender": "FEMALE"}
[KSTREAM-SOURCE-0000000000]: User_22, {"registertime": 1509434179667, "userid": "User_22", "regionid": "Region_6", "gender": "FEMALE"}
[KSTREAM-SOURCE-0000000000]: User_52, {"registertime": 1503698765232, "userid": "User_52", "regionid": "Region_2", "gender": "FEMALE"}
```
