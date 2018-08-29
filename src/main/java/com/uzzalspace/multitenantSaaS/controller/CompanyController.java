package com.uzzalspace.multitenantSaaS.controller;

import com.uzzalspace.multitenantSaaS.dao.Product;
import com.uzzalspace.multitenantSaaS.dao.ProductRepository;
import com.uzzalspace.multitenantSaaS.tenant.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

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
