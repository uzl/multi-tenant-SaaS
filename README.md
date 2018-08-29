# multi-tenant-SaaS

In the Software as a Service (SaaS) model, your company does not sell licenses to your software. Instead, each customer makes rent payments to your company, making each customer a tenant of your company.

A tenancy model determines how each tenant’s data is mapped to storage.
The term tenancy model refers to how tenants' stored data is organized:

- Single-tenancy:  Each database stores data from only one tenant.
- Multi-tenancy:  Each database stores data from multiple separate tenants (with mechanisms to protect data privacy).
- Hybrid tenancy models are also available.

#### Standalone single-tenant app with single-tenant database
In this model, the whole application is installed repeatedly, once for each tenant.
<img src="doc/img/saas-standalone-app-single-tenant-database-11.png">
 
<hr>

### Reference 
[Multi-tenant SaaS database tenancy patterns - microsoft](https://docs.microsoft.com/en-us/azure/sql-database/saas-tenancy-app-design-patterns)