package com.baidu.umstm.test;

import ma.glasnost.orika.MapperFacade;

import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.MapperFactory;
import junit.framework.TestCase;

public class TestObjMapper extends TestCase {
    public void testMapper() {
        User u = new User();
        u.age = 10;
        u.name = "zhangbo";
        Address ad = new Address();
        ad.address = "men pai";
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        mapperFactory.classMap(User.class, UserDto.class).field("age", "age1").field("name", "name1").byDefault()
                .register();
        mapperFactory.classMap(Address.class, UserDto.class).field("address", "address");
        MapperFacade mapper = mapperFactory.getMapperFacade();

        UserDto ut = mapper.map(u, UserDto.class);
        mapper.map(ad, ut);
        assertEquals(u.age, ut.age1);
        assertEquals(u.name, ut.name1);
        assertEquals(ad.address, ut.address);
        User u1 = mapper.map(ut, User.class);
        assertEquals(u1.age, ut.age1);
        assertEquals(u1.name, ut.name1);
        assertEquals(u1.address.address, ut.address);
    }
}
