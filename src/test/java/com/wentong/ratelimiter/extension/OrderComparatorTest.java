package com.wentong.ratelimiter.extension;

import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class OrderComparatorTest {

  @Order(Order.HIGHEST_PRECEDENCE + 10)
  public class A {}

  @Order(Order.HIGHEST_PRECEDENCE + 20)
  public class B {}

  public class C {}

  @Order(Order.LOWEST_PRECEDENCE+ 10)
  public class D {}

  public void testCompare() {
    int result = OrderComparator.INSTANCE.compare(new A(), new B());
    Assert.assertTrue(result<0);

    result = OrderComparator.INSTANCE.compare(new A(), new A());
    Assert.assertTrue(result==0);

    result = OrderComparator.INSTANCE.compare(new B(), new A());
    Assert.assertTrue(result>0);

    result = OrderComparator.INSTANCE.compare(new A(), new C());
    Assert.assertTrue(result<0);
  }

  @Test(expectedExceptions = { IndexOutOfBoundsException.class })
  public void testCompare_withInvalidOrder() throws IndexOutOfBoundsException {
    OrderComparator.INSTANCE.compare(new A(), new D());
  }

}
