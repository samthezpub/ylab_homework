package ylab.com.hw;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "dir=.",
    "min-length=3",
    "top=5"
})
class HwApplicationTests {

  @Test
  void contextLoads() {
  }

}
