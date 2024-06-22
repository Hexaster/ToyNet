package Data;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.List;

@Configuration
@ComponentScan(basePackages = "Data")
public class DataConfig {

    @Bean
    @Scope(value = "prototype")
    public Tensor tensorArray(List<?> array) {
        return new Tensor(array);
    }

    @Bean
    @Scope(value = "prototype")
    public Tensor tensorDir(DoubleArrayList data, IntArrayList shape) {
        return new Tensor(data, shape);
    }
}
