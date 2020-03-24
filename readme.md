# Simple CQRS with event-sourcing: Tracking order service

The following repository tries to describe a basic CQRS with Kotlin and Ktor as a base framework, our main goal is
 understand through a simple implementation this pattern.
 
**This is not production code, this is a CQRS implementation in order to understand the pattern.**

Also, I am not an expert on the topic, not even a beginner, so this is a way to learn.
 
 
## CQRS overview

CQRS stands for Command Query Responsibility Segregation, and it is a pattern introduced by [Greg Young at 2010](https://cqrs.files.wordpress.com/2010/11/cqrs_documents.pdf),
 it is also an extension from an older concept, [CQS](https://en.wikipedia.org/wiki/Command%E2%80%93query_separation) introduce by by Bertrand Meyer.
 
Conceptually, CQRS is very simple, the basic idea is to separate the querying of data (read-side/query) from the
 updating of data (write-side/commands) models.
 
But, let's see a more elaborate but brief explanation in the next sections ...
 
### App without CQRS

The typical approach of an application/micro-service without applying CQRS looks like this:

<p align="center">
  <img src="misc/NO-CQRS.png"  width="55%"/>
</p>

There is one model, where the app performs writes and reads and we use the same classes for same use cases.

We can apply any architectural pattern like n-layered, hexagonal, onion or clean architectures on top of that but the
 main idea would remain the same.

### Simple CQRS

In it's simplest form CQRS only needs to segregate the responsibility between commands (write requests) and 
queries (read requests). The write requests and the read requests are handled by different objects.

<p align="center">
  <img src="misc/Simple-CQRS.png"  width="55%"/>
</p>

One write model, which you can use to change the state of your domain objects. Then multiple read models, one for
 each client. 
 
That way, there will be less coupling, so better Separation of Concerns and it will be easy to evolve the model in any direction. Almost
 any complex business could belong the write model and the read model can be more simple.

But let's evolve this to next step ...

### CQRS with separate storage engines

The next natural step is to separate the storage as well in two sides, the write side and the read side.

<p align="center">
  <img src="misc/CQRS.png"  width="55%"/>
</p>

The write side represents the state of the application and the read side are the projections (schemas optimized to
 simplify the read for a given consumer). We can even go one step further and have two different deployable applications.

Projection mechanisms are the tools/processes that get the changes from the write database, prepare and update the
 projections; They could be anything that works, from buses, event logs or just schedulers for query and update.  

The new advantages of this separation are:

- Scalability: we can scale independently both sides depending on the workload
- Performance: we can apply different tools and storage engines that fit better for each case.
- Query simplification. The read side can use a storage engine and schema that is optimized for queries.

But, sadly it also comes with drawbacks:

- Complexity: Now we have a more complex application design.
- Eventual consistency: Now the read side will be eventually consistent, so we will have to deal with distributed
 transactions. 
- **Dual writes**: A dual write describes the situation when you change data in 2 systems using local transactions, for
 example a database and queue or other database, without an additional layer that ensures data consistency over both
  services. In almost all the post that you can find over internet about CQRS, this problem is underestimated but it
   can lead you to a very inconsistent state of your system. 

The only way to avoid dual writes is to split the communication into multiple steps and only write to one
 external system during each step, here some solutions:

- Do it by yourself, if you use a RDBMS you can create an event-log table, then you can wrap the updates in your domain
 and the event-log in the same transaction. After that you can fetch the events with simple schedulers or complex (Schedlock) 
 and send a message to the projection mechanism. 
 Note: Your projection mechanism will have to handle with idempotency because the fetch can also fail.
- Use [CDC](https://en.wikipedia.org/wiki/Change_data_capture) tools (change data capture), like Debezium, they will
 capture and propagate the changes ensuring consistency.
- Use an event-store in the write side, this will be explained in the following section.  

### CQRs with event sourcing

Last step is to introduce [event sourcing](https://microservices.io/patterns/data/event-sourcing.html), with this
 approach we would remove the dual write problem but also we will introduce new advantages and new drawbacks. The
  idea is the same as the previous approach but using an event store.

<p align="center">
  <img src="misc/CQRS-ES.png"  width="55%"/>
</p>

Instead of keeping only the current state of the system updating it when we have a change, we will represent
 entities as a sequential list of events.

Till now, we have implemented CQRS, but usually CQRS doesn't come alone in term of patterns, so let's see how the
 system would look like with everything:

<p align="center">
  <img src="misc/CQRS-ES-Extended.png"/>
</p>

Patterns that come with CQRS:
- DDD Aggregates
- Command-bus
- Events 
- SAGAs
- Optimistic locking

Any of these patterns are mandatory to do CQRS, it does not imply to use any of them, in prod environments you can omit
 them if they are over-complicating your system.

Check [here](http://www.cqrs.nu/faq) to see a brief explanation of these patterns!  

## Service


### Applying CQRS

## Related links

- [General FAQ](http://www.cqrs.nu/faq)
- [Greg Young original document](https://cqrs.files.wordpress.com/2010/11/cqrs_documents.pdf)
- [Martin Fowler about CQRS](https://martinfowler.com/bliki/CQRS.html)
- [Good article about when to use it and pros & cons](https://docs.microsoft.com/en-us/azure/architecture/patterns/cqrs)
- [Event Sourcing](https://microservices.io/patterns/data/event-sourcing.html)
- [Things to consider](https://www.sderosiaux.com/articles/2019/08/29/cqrs-why-and-all-the-things-to-consider/)
- [DZone CQRS intro](https://dzone.com/articles/cqrs-and-event-sourcing-intro-for-developers)
