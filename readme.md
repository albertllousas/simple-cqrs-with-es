# CQRS, event-sourcing, DDD and hexagonal architecture: Tracking order service

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
- Dual writes

#### Dual writes
 A dual write describes the situation when you change data in 2 systems using local transactions, for
 example inserting a record in a database and send a message to a queue in a local transaction; 
 Imagine a situation like this:
```kotlin
transaction.begin() 
database.insert(record) 
queue.send(message)
transaction.commit() 
``` 
Now, let's say that this codes is executed, but transaction commit fails, without an additional layer that ensures
 data consistency over both services, there is no way to rollback the message if it was already sent.
 
 In almost all the post that you can find over internet about CQRS, this problem is underestimated but it can lead you 
 to a very inconsistent state of your system. 

The way to solve/avoid dual writes is to split the communication into multiple steps and only write to one
 external system during each step, here some solutions:

- [Transactional outbox pattern](https://microservices.io/patterns/data/transactional-outbox.html):If you use a RDBMS
 you can create an event-log table, then you can wrap the updates in your domain and the event-log in the same
  transaction. After that you can fetch the events with simple schedulers or complex (Schedlock) and send a message to the projection mechanism. 
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
- Optimistic locking

Any of these patterns are mandatory to do CQRS, you don't have to use them, in prod environments you can omit them if
 they are over-complicating your system.
 
It is important to mention, that as Martin fowler explains in this talk, you don't need even to make commands async, 

Check [here](http://www.cqrs.nu/faq) to see a brief explanation of these patterns!  

## Simple todo-list Service

Let's think about a fake company, they have an internal micro-services ecosystem and they need to build a new
 feature for all the frontends ... Boom! a TODO-LIST to help the customers to keep track of daily obligations.

So, our sample application is a super simple TODO-LIST micro-service, where:

MVP:
```
- A todo-list can be created 
- We can add a task to a todo list
- We can mark a task as done
- When all tasks on a todo-list are done, automatically, todo-list will be marked a as done as well 
```
Second iteration:
```
- Rename todo-lists
- Rename tasks
- Delete todo-lists
- Delete tasks
- Prioritise tasks
```

### Domain model with DDD

I will try to apply DDD concepts, sorry if I make mistakes but I am not an expert.

Let's map out our business domain for the MVP:

- Aggregate Root: `TodoList`
- Entities: `Task`
- Value objects: `Status`, `TodoListId`, `TaskId`, `UserId` (user reference)
- Factories: `TodoList.Factory`
- Aggregate: All of them

DDD [definitions](https://dddcommunity.org/resources/ddd_terms/)

### Event sourcing

In a nutshell, event sourcing differs from a typical approach on how a business object is persisted, instead of
 storing the current state, it stores a sequence of state changing events.

This restriction has some implications in the design:

- Domain objects should generate events when it's state changes.
- Domain objects should be able to be reconstructed from an event stream.
- We will need an event-store.
- Our repositories will just `get` and `save` aggregates, but under the hood it will be streams of events.
- And more implications and complexities ... check the links at the end for more information about event sourcing.

These are the events that we will handle: `TodoListCreated`, `TaskAdded`, `TaskFinished`, `TodoListFinished`


### Fitting command-side in hexagonal architecture

Till now we have CQRS, DDD and event-sourcing, but now we have to fit everything over an architectural pattern, and
 in our case it is hexagonal architecture, check this [other repo on my github](https://github.com/albertllousas/implementing-hexagonal-architecture) for a detailed explanation of the
  pattern.

Trying to fit all the components in an hexagonal architecture is not easy at all, but let's see how a general diagram
 of cqrs over hexagonal would look like (since query-side is easier, just pay attention in the command side):
 
<p align="center">
  <img src="misc/cqrs-hexa-1.png"  width="85%"/>
</p> 

And now let's focus in our problem (only command-side):

<p align="center">
  <img src="misc/cqrs-hexa-2.png"  width="95%"/>
</p> 

### Simple Query-side

Hexagonal architecture is a domain-centric architectural pattern, but since query side does not have
 domain or any business logic, do we need an hexagonal approach? 

Query side, as CQRS trend advocates to, should be as thin as possible, getting as close to the data store as possible.

So, what are the responsibilities of the query side? 

1. Read [projections](http://cqrs.wikidot.com/doc:projection), using event handlers
2. Update [projections](http://cqrs.wikidot.com/doc:projection), using query handlers
 
If we think about it, there is no domain modeling or business involved here, it is just update and read views.

Having said that, the query side should be as lean/thin as we can.

Since views/projections are isolated, meaning that they can be accessed and updated without any other dependency than
 the stream of event and the storage, a simple [package-by-feature] would be a good way to structure the project.
 
Architecturally, query side would be:

<p align="center">
  <img src="misc/query-side.png"  width="65%"/>
</p> 

### Package structure

The project has been split in three different modules:
- `command-side`: All the write side code
- `query-side`: read side code
- `app`: Wiring up and app runner

Projects follow the Hexagonal architecture with this packaging:

- Application: Application Services (the use cases), in our case as command-handlers
- Domain model: domain and ports
- Infrastructure: Outside world, adapters and non-hexagonal components 

Package structure for command-side:


## tech stack

* Language: Kotlin
* JVM: 1.8.0*
* Web server: [Ktor](https://ktor.io/)
* Testing libraries/frameworks:
    * [JUnit 5](https://junit.org/junit5/docs/current/user-guide/)
    * [Assertj](https://joel-costigliola.github.io/assertj/)
    * [Mockk](https://mockk.io/)
    * [REST Assured](http://rest-assured.io/)

## Running tests
```shell
./gradlew test
```
## Run the app

// TODO

## Related links

- [CQRS](http://www.cqrs.nu/)
- [Greg Young original document](https://cqrs.files.wordpress.com/2010/11/cqrs_documents.pdf)
- [Greg Young m-r sample](https://github.com/gregoryyoung/m-r)
- [Martin Fowler about CQRS](https://martinfowler.com/bliki/CQRS.html)
- [Good article about when to use it and pros & cons](https://docs.microsoft.com/en-us/azure/architecture/patterns/cqrs)
- [Event Sourcing](https://microservices.io/patterns/data/event-sourcing.html)
- [Event Sourcing by Martin Fowler](https://martinfowler.com/eaaDev/EventSourcing.html)
- [Things to consider](https://www.sderosiaux.com/articles/2019/08/29/cqrs-why-and-all-the-things-to-consider/)
- [DZone CQRS intro](https://dzone.com/articles/cqrs-and-event-sourcing-intro-for-developers)
