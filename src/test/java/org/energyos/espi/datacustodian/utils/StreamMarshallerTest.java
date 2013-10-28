package org.energyos.espi.datacustodian.utils;

import org.energyos.espi.datacustodian.BaseTest;
import org.energyos.espi.datacustodian.models.atom.EntryType;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import javax.xml.bind.JAXBElement;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StreamMarshallerTest extends BaseTest {
    @Mock
    Jaxb2Marshaller marshaller;

    @Test
    public void unmarshalStream_returnsEntryType() {
        InputStream inputStream = mock(InputStream.class);

        StreamMarshaller streamMarshaller = new StreamMarshaller();
        streamMarshaller.setMarshaller(marshaller);

        JAXBElement jaxbElement = mock(JAXBElement.class);
        EntryType entryType = new EntryType();
        when(jaxbElement.getValue()).thenReturn(entryType);
        when(marshaller.unmarshal(any(StreamSource.class))).thenReturn(jaxbElement);

        assertThat(streamMarshaller.unmarshal(inputStream, EntryType.class), is(entryType));
    }

    @Test
    public void unmarshalString_returnsEntryType() {
        StreamMarshaller streamMarshaller = new StreamMarshaller();
        streamMarshaller.setMarshaller(marshaller);

        JAXBElement jaxbElement = mock(JAXBElement.class);
        EntryType entryType = new EntryType();
        when(jaxbElement.getValue()).thenReturn(entryType);
        when(marshaller.unmarshal(any(StreamSource.class))).thenReturn(jaxbElement);

        assertThat(streamMarshaller.unmarshal("String", EntryType.class), is(entryType));
    }


}
