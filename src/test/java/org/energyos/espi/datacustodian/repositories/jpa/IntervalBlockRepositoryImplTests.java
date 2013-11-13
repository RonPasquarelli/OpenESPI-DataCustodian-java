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

package org.energyos.espi.datacustodian.repositories.jpa;


import org.energyos.espi.datacustodian.domain.IntervalBlock;
import org.energyos.espi.datacustodian.domain.MeterReading;
import org.energyos.espi.datacustodian.repositories.IntervalBlockRepository;
import org.energyos.espi.datacustodian.repositories.MeterReadingRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring/test-context.xml")
@Transactional
public class IntervalBlockRepositoryImplTests {

    @Autowired
    protected IntervalBlockRepository repository;
    @Autowired
    protected MeterReadingRepository meterReadingRepository;

    @Test
    public void findByMeterReadingId_returnsIntervalBlocks() {
        MeterReading meterReading = new MeterReading();
        meterReading.setUUID(UUID.randomUUID());

        IntervalBlock intervalBlock = new IntervalBlock();
        intervalBlock.setUUID(UUID.randomUUID());

        IntervalBlock intervalBlock1 = new IntervalBlock();
        intervalBlock1.setUUID(UUID.randomUUID());

        meterReading.addIntervalBlock(intervalBlock);
        meterReading.addIntervalBlock(intervalBlock1);

        meterReadingRepository.persist(meterReading);

        assertEquals(2, repository.findAllByMeterReadingId(meterReading.getId()).size());
    }
}