package io.sunshower.barometer.jaxrs;

import org.junit.Test;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static org.junit.Assert.*;

/**
 * Created by haswell on 10/26/16.
 */
public class SerializationAwareTest {

    @XmlRootElement
    public static class TestEntity {
        @XmlAttribute
        private String firstName;
        @XmlElement
        private String lastName;
    }

    @Test
    public void ensureWritingEntityToJsonWorks() {
        final TestEntity te = new TestEntity();
        te.firstName = "joe";
        te.lastName = "haswell";
        System.out.println(SerializationAware.write(te, SerializationAware.Format.JSON));
    }

    @Test
    public void ensureWritingEntityToXmlWorks() {
        final TestEntity te = new TestEntity();
        te.firstName = "joe";
        te.lastName = "haswell";
        System.out.println(SerializationAware.write(te, SerializationAware.Format.XML));
    }
}