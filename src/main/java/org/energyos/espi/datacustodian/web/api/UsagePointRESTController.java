package org.energyos.espi.datacustodian.web.api;
/*
 * Copyright 2013 EnergyOS.org
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

import com.sun.syndication.io.FeedException;
import org.energyos.espi.common.domain.RetailCustomer;
import org.energyos.espi.common.domain.Routes;
import org.energyos.espi.common.domain.UsagePoint;
import org.energyos.espi.common.service.RetailCustomerService;
import org.energyos.espi.common.service.UsagePointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Controller
public class UsagePointRESTController {

    @Autowired
    private UsagePointService usagePointService;
    @Autowired
    private RetailCustomerService retailCustomerService;
    @Autowired
    private AtomService atomService;

    @RequestMapping(value = Routes.DATA_CUSTODIAN_REST_USAGE_POINT_COLLECTION, method = RequestMethod.GET)
    public void index(HttpServletResponse response, @PathVariable long retailCustomerId) throws IOException, FeedException {
        RetailCustomer retailCustomer = retailCustomerService.findById(retailCustomerId);
        List<UsagePoint> usagePoints = usagePointService.findAllByRetailCustomer(retailCustomer);
        response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);

        response.getWriter().print(atomService.feedFor(usagePoints));
    }

    @RequestMapping(value = Routes.DATA_CUSTODIAN_REST_USAGE_POINT_MEMBER, method = RequestMethod.GET)
    public void show(HttpServletResponse response, @PathVariable String usagePointHashedId) throws IOException, FeedException {
        UsagePoint usagePoint = usagePointService.findByHashedId(usagePointHashedId);
        response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);

        response.getWriter().print(atomService.entryFor(usagePoint));
    }

    @RequestMapping(value = Routes.DATA_CUSTODIAN_REST_USAGE_POINT_CREATE, method = RequestMethod.POST)
    public void create(HttpServletResponse response, @PathVariable long retailCustomerId, InputStream stream) throws IOException {
        RetailCustomer retailCustomer = retailCustomerService.findById(retailCustomerId);

        try {
            UsagePoint usagePoint = this.usagePointService.importUsagePoint(stream);
            usagePointService.associateByUUID(retailCustomer, usagePoint.getUUID());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
    }

    @RequestMapping(value = Routes.DATA_CUSTODIAN_REST_USAGE_POINT_UPDATE, method = RequestMethod.PUT)
    public void update(HttpServletResponse response, @PathVariable long retailCustomerHashedId, @PathVariable String usagePointHashedId, InputStream stream) {
        RetailCustomer retailCustomer = retailCustomerService.findById(retailCustomerHashedId);
        UsagePoint existingUsagePoint;

        existingUsagePoint = loadUsagePoint(response, retailCustomer, usagePointHashedId);

        if (existingUsagePoint != null) {
            try {
                UsagePoint usagePoint = this.usagePointService.importUsagePoint(stream);
                usagePointService.associateByUUID(retailCustomer, usagePoint.getUUID());
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            }
        }
    }

    @RequestMapping(value = Routes.DATA_CUSTODIAN_REST_USAGE_POINT_UPDATE, method = RequestMethod.DELETE)
    public void delete(HttpServletResponse response, @PathVariable long retailCustomerHashedId, @PathVariable String usagePointHashedId) {
        RetailCustomer retailCustomer = retailCustomerService.findById(retailCustomerHashedId);

        UsagePoint existingUsagePoint = loadUsagePoint(response, retailCustomer, usagePointHashedId);

        if (existingUsagePoint != null) {
            this.usagePointService.deleteByHashedId(usagePointHashedId);
        }
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleEmptyResultDataAccessException() {
        return "400";
    }

    private UsagePoint loadUsagePoint(HttpServletResponse response, RetailCustomer retailCustomer, String usagePointHashedId) {
        UsagePoint usagePoint;
        UsagePoint existingUsagePoint = null;

        usagePoint = usagePointService.findByHashedId(usagePointHashedId);

        if (null != usagePoint) {
            if (usagePoint.getRetailCustomer().equals(retailCustomer)) {
                existingUsagePoint = usagePoint;
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }

        return existingUsagePoint;
    }

    public void setUsagePointService(UsagePointService usagePointService) {
        this.usagePointService = usagePointService;
    }

    public void setRetailCustomerService(RetailCustomerService retailCustomerService) {
        this.retailCustomerService = retailCustomerService;
    }

    public void setAtomService(AtomService atomService) {
        this.atomService = atomService;
    }
}
