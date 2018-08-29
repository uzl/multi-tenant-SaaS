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



### Reference 
[Multi-tenant SaaS database tenancy patterns - microsoft](https://docs.microsoft.com/en-us/azure/sql-database/saas-tenancy-app-design-patterns)