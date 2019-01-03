# simplekafka
Simple Kafka example, will be creating example of Joining 2 streams and storing values to DB.

# Generate sample data in "Users" topic
`$ ksql-datagen quickstart=users topic=users maxInterval=1000 schemaRegistryUrl="http://localhost:8081"  format=avro`

# Validate values on console
` ksql-datagen topic=users maxInterval=1000 schemaRegistryUrl="http://localhost:8081"  format=avro schema=users.avro key=userid`
```
User_20 --> ([ 1516490143893 | 'User_20' | 'Region_1' | 'MALE' ]) ts:1546493751045
User_10 --> ([ 1508919732379 | 'User_10' | 'Region_4' | 'OTHER' ]) ts:1546493751299
User_42 --> ([ 1511263168089 | 'User_42' | 'Region_5' | 'OTHER' ]) ts:1546493752220
```

# Validate schema on schema-registry
`$ curl "http://localhost:8081/subjects"`
`["users-value"]`

# Get schema of topic values
`$ curl "http://localhost:8081/subjects/users-value/versions"`
`[1,2,3]`

`# curl "http://localhost:8081/subjects/users-value/versions/3"`
```
{"subject":"users-value","version":3,"id":7,"schema":"{\"type\":\"record\",\"name\":\"KsqlDataSourceSchema\",\"namespace\":\"io.confluent.ksql.avro_schemas\",\"fields\":[{\"name\":\"userid\",\"type\":[\"null\",\"string\"],\"default\":null}]}"}
```

# Format schema to use in downstream code
```
{
	"type": "record",
	"name": "Users",
	"namespace": "com.example",
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
