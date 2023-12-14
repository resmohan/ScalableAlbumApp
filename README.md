# Music Album Review System: A Scalable and Distributed System for Posting and Retrieving Album Information

This project is a scalable and distributed system for posting and retrieving album information using REST APIs. It allows users to create albums with metadata such as title, artist year, album image, etc., and review albums with likes and dislikes. It also enables users to search for albums by album ids which are the unique identifiers returned while creating an album. 

When post api is invoked to create a new album, the data will be stored in DynamoDB after performing necessary validations. Album image will be stored in S3 and related metadata will be stored in dynamo db itself.

When review post api is invoked, information related to album review information are moved to a rabbitmq messaging service. Consumer threads are available to move the data from rabbitmq cluster to dynamo db.

Here is the high level overview of major classes and their relations.
# Server side
a)	[DatabaseClient:](MusicAlbumApp/src/main/java/db/DatabaseClient.java) Helps in establishing connection with Amazon dynamodb and to create/update/read the album and review data.

b)	[ConnectionHandler:](MusicAlbumApp/src/main/java/message/ConnectionHandler.java) Helps in establishing connection with RabbitMQ server.

c)	[ConnectionPoolHandler:](MusicAlbumApp/src/main/java/message/ConnectionPoolHandler.java) Once ConnectionHandler establishes connection with RabbitMQ server, it helps to create a pool of channels that can be used to publish message on RabbitMQ server.

d)	[AlbumReview:](MusicAlbumApp/src/main/java/model/AlbumReview.java) Helps to track the structure of message written on RabbitMQ queue. It stores the album id and a boolean indicating if it is a like or dislike.

e)	[MessagePublisher:](MusicAlbumApp/src/main/java/message/MessagePublisher.java) Helps to publish a message to RabbitMQ server. It takes a channel from ConnectionPoolHandler, publish the message on a message queue using that channel and then release the channel back to pool.

f)	[MessageProcessor:](MusicAlbumApp/src/main/java/message/MessageProcessor.java) Helps to process a message, invoke the database client, and update the album review information. This class implements runnable interface. It will run in a loop until it gets a signal to stop further message processing.

g)	[MessageHandler:](MusicAlbumApp/src/main/java/message/MessageHandler.java) Helps to consume a message from message queue. This class implements runnable interface. It will create a channel and consume the message when available. When a message arrives, it will send back an acknowledgement and store the message in a blocking queue so that MessageProcessor can process it.

h)	[MessageConsumer:](MusicAlbumApp/src/main/java/message/MessageConsumer.java) Establishes connection with RabbitMQ server and database using ConnectionHandler and DatabaseClient respectively. Then invokes a set of MessageHandler and MessageProcessor threads, so that messages can be consumed and processed in parallel.

i)	[ReviewDataHandler:](MusicAlbumApp/src/main/java/util/ReviewDataHandler.java) This is the helper class that helps to initiate the database and rabbitmq server connections. It also helps to invoke the MessagePublisher to publish a message when a post request comes with review information.

j)	[AlbumReviewServlet:](MusicAlbumApp/src/main/java/servlet/AlbumReviewServlet.java) Helps in handling the post request to like/dislike an album. When servlet is initialized, it creates a new instance of ReviewDataHandler and invoke the MessageConsumer to do the initialization steps. Once a post request comes, it does some basic validation, pass the information to ReviewDataHandler and then sends back the response. During the servlet destroy phase, it invokes the resource shutdown process in ReviewDataHandler and MessageConsumer.

k)	[AlbumDataHandler:](MusicAlbumApp/src/main/java/util/AlbumDataHandler.java) This is the helper class that helps to initiate the database and s3 server connections.

l)	[AlbumReviewServlet:](MusicAlbumApp/src/main/java/servlet/AlbumReviewServlet.java) Helps in handling the album post request. When servlet is initialized, it creates a new instance of AlbumDataHandler and invoke the DatabaseClient to do the initialization steps. Once a post request comes, it does some basic validation, pass the information to AlbumDataHandler and then sends back the response.

# Client side
a)	[ClientHandler:](MusicAlbumClient/src/main/java/connection/ClientHandler.java) Helps in establishing a connection with server, execute the request and retry if the response code lies in the range of 4xx and 5xx.

b)	[RequestHandler:](MusicAlbumClient/src/main/java/connection/RequestHandler.java) Helps in creating a GET or POST request and building the request body content.

c)	[RequestContent:](MusicAlbumClient/src/main/java/connection/RequestContent.java) Gives a structure on the request body content. User can pass the data based on this structure and the corresponding body content will be generated by RequestHandler class.

d)	[ContentType:](MusicAlbumClient/src/main/java/connection/ContentType.java) Enum class that helps to define the supported content types in a request body.

e)	[ResponseHandler:](MusicAlbumClient/src/main/java/connection/ResponseHandler.java) Helps to analyze the response content and structures the data as defined in ResponseContent class.

f)	[ResponseContent:](MusicAlbumClient/src/main/java/connection/ResponseContent.java) Specifies the structure of response content that can be extracted from a response.

g)	[Album:](MusicAlbumClient/src/main/java/model/Album.java) Defines the structure of album data

h)	[ImageInfo:](MusicAlbumClient/src/main/java/model/ImageInfo.java) Defines the structure of album response content.

i)	[BaseServiceThread:](MusicAlbumClient/src/main/java/util/BaseServiceThread.java) An abstract class that helps to define any contract methods required for ServiceThread classes defined in client part1 and part2. ServiceThread classes in client part1 and part2 will be extending this, so that the ThreadHandler class can invoke the corresponding service thread class.

j)	[Constants:](MusicAlbumClient/src/main/java/util/Constants.java) Class which helps to define the constant values used in this project

k)	[CsvWriter:](MusicAlbumClient/src/main/java/util/CsvWriter.java) Class which helps to write the latency information and data required for graph plotting to csv files.

l)	[PerformanceTracker:](MusicAlbumClient/src/main/java/util/PerformanceTracker.java) It helps to track the latency related information like wall time, throughput and helps in calculating the performance statistics.

m)	[RequestType:](MusicAlbumClient/src/main/java/util/RequestType.java) An enum class that defines the set of http requests supported.

n)	[ResponseLatency:](MusicAlbumClient/src/main/java/util/ResponseLatency.java) It defines the structure of response latency details that needs to be stored.

o)	[ServiceInvoker:](MusicAlbumClient/src/main/java/util/ServiceInvoker.java) Class that helps to invoke get and post requests.

p)	[ThreadHandler:](MusicAlbumClient/src/main/java/util/ThreadHandler.java) Class that helps to invoke the threads configured by the user.

q)	[ServiceHandler:](MusicAlbumClient/src/main/java/client/part4/ServiceHandler.java) Main class that helps to capture the arguments specified by the user and start the logic.

r)	[ServiceThread:](MusicAlbumClient/src/main/java/client/part4/ServiceThread.java) Class which is being executed by each thread.
