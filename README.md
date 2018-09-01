# Multi-tenant SaaS Database Tenancy Patterns

## Introduction to Database Tenancy
In the Software as a Service (SaaS) model, your company does not sell licenses to your software. Instead, each customer makes rent payments to your company, making each customer a tenant of your company.

A tenancy model determines how each tenant’s data is mapped to storage.

#### Standalone single-tenant app with single-tenant database
In this model, the whole application is installed repeatedly, once for each tenant.
<img src="doc/img/saas-standalone-app-single-tenant-database-11.png">
 
<hr>

#### Multi-tenant app with database-per-tenant
In this model A new database is provisioned for each new tenant. 

<img src="doc/img/saas-multi-tenant-app-database-per-tenant-13.png">

When databases are deployed in the same resource group, they can be grouped into elastic database pools. This pool option is cheaper and still achieve a high degree of performance isolation.

<img src="doc/img/saas-multi-tenant-app-database-per-tenant-pool-15.png">

<hr>

#### Multi-tenant app with sharded multi-tenant databases
This access pattern allows tenant data to be distributed across multiple databases or shards, where all the data for any one tenant is contained in one shard. Combined with a multi-tenant database pattern, a sharded model allows almost limitless scale.
<img src="doc/img/saas-multi-tenant-app-sharded-multi-tenant-databases-17.png">

<hr>
<hr>

## Our Code
**We will implement our SaaS application with database-per-tenant**

*For this implementation, we will use spring boot's `AbstractRoutingDataSource` features for dynamic database switching.* 

Let's imagine, we want to store some car models from two company **Tesla** and **BMW** into our database. We have two company, so we will use two database. Depending on the company name, our product will be stored in their corresponding isolated database.

If we use URL `localhost:8080/tesla` then our SaaS application will use `dbtenant1` database.

If we use URL `localhost:8080/bmw` then our SaaS application will use `dbtenant2` database.



We need two sample tenant database named `dbtenant1`  and `dbtenant2`. For simplicity, we have created one `product` table in both database. 
```sql
create table product
(
  id    int  auto_increment  primary key,
  name  varchar(255) null,
  price float        null,
  code  varchar(255) not null
);
```

Now, We are going to store car information in our SaaS Application. So, we will create a POST request containing some JSON value.

<img src="doc/img/tesla.PNG">
  


The controller is very simple. It receives a path variable which is actually our tenant key (`tesla` or `bmw`).
Then tenant key is then saved in ``ThreadLocal`` calling `setCurrentTenant(company)` method. 
```java

@RestController
public class CompanyController {

    @Autowired
    ProductRepository repository;

    @RequestMapping(value="/{company}", method=RequestMethod.POST)
    public Object findOwner(@PathVariable String company, @RequestBody Product product) {

        // set the company name(tenant name)
        TenantContext.setCurrentTenant(company);

        // create dummy product code
        product.setCode( company + "_" + new Random().nextInt(100));

        // save to database
        Product savedProdict = repository.save(product);

        return savedProdict;
    }
}

```

So when the request just receive in controller, we immediately set the tenant name before any database operation. 
Setting the tenant name is straightforward. In ``TenantContext`` class, a setter method is used for storing in `ThreadLocal`, and a getter method will be used during database operation. We used `ThreadLocal` because it is only accessible from current request thread, and will not mix with another request by other person. 
     
```java

public class TenantContext {
    private static ThreadLocal<Object> currentTenant = new ThreadLocal<>();

    public static void setCurrentTenant(Object tenant) {
        currentTenant.set(tenant);
    }

    public static Object getCurrentTenant() {
        return currentTenant.get();
    }
}
```


Now let's move on database switching part. Here we used `AbstractRoutingDataSource` feature of spring framework.
 

By defination, [AbstractRoutingDataSource](https://docs.spring.io/spring/docs/3.2.5.RELEASE/javadoc-api/org/springframework/jdbc/datasource/lookup/AbstractRoutingDataSource.html) implementation routes getConnection() calls to one of various target DataSources based on a lookup key. The latter is usually (but not necessarily) determined through some thread-bound transaction context.

In our code, we extends `AbstractRoutingDataSource` to override the `determineCurrentLookupKey()` method and get the datasource name from current thread to meet our need. 
```java

public class MultitenantDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        return TenantContext.getCurrentTenant();
    }
}

```

But, before lookup from DataSource, we need mapping between lookup key(ex. tesla or bmw) and datasource propertices(ex. database name, username, password).


in our code `resolvedDataSources` is a map of datasource. As much tenant database we need, we have to put those here. 
We also need a default datasource which should not use as tenant database purpose.

```java

MultitenantDataSource dataSource = new MultitenantDataSource();
dataSource.setDefaultTargetDataSource(defaultDataSource());
dataSource.setTargetDataSources(resolvedDataSources);
```

But where do we write database credetial for datasource? 

Here, we have a folder named `tenant`. As we need two tenant database, two properties are kept inside `tenant` folder in project root location, and each one contains database credentials. One for `tesla`, other one for `bmw`. 

<img src="doc/img/tenant_property.PNG">


These properties files have very simple structure. For example `tenant_a.properties` looks like this
```properties
name=tesla
datasource.url=jdbc:mysql://localhost:3306/dbtenant1
datasource.username=root
datasource.password=
```
Here, `name=tesla` key will map `dbtenant1` database. So, if you need more tenant database, just create one more properties file.
<hr>

### Limitations
If you want to add one more car company (I mean, tenant database) you have to create one properties file and also restart the application. 

### Reference 
[Multi-tenant SaaS database tenancy patterns - by Microsoft](https://docs.microsoft.com/en-us/azure/sql-database/saas-tenancy-app-design-patterns)