package br.com.gynlog.app;

import br.com.gynlog.view.TelaPrincipal;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import javax.swing.SwingUtilities;

@SpringBootApplication(scanBasePackages = "br.com.gynlog")
public class GynLogApp {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(GynLogApp.class)
                .headless(false)
                .web(WebApplicationType.NONE)
                .run(args);

        SwingUtilities.invokeLater(() -> {
            TelaPrincipal tela = new TelaPrincipal(context);
            tela.setVisible(true);
        });
    }
}