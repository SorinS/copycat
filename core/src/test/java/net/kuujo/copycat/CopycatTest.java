package net.kuujo.copycat;

import static org.testng.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.testng.annotations.Test;

@Test
public class CopycatTest extends AbstractCopycatTest {
  public void shouldStartAndStop() throws Throwable {
    Copycat copycat = buildCluster(1).get(0);
    AtomicInteger eventCount = new AtomicInteger();

    copycat.on().start().run(e -> {
      eventCount.incrementAndGet();
    });
    copycat.on().stop().run(e -> {
      eventCount.incrementAndGet();
    });

    copycat.start();
    copycat.stop();

    assertEquals(stateChanges,
        Arrays.asList(CopycatState.NONE, CopycatState.LEADER, CopycatState.NONE));
    assertEquals(eventCount.get(), 2);
  }

  public void shouldStartAndStopCluster() throws Throwable{
    List<Copycat> cluster = buildCluster(2);
    cluster.stream().forEach(c -> c.start());
    Thread.sleep(10000000);
    //cluster.stream().forEach(c -> c.stop());
  }
}
