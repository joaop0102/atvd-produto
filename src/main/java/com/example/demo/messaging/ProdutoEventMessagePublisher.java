package com.example.demo.messaging;

import com.example.demo.event.ProdutoAlteradoEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@ConditionalOnProperty(name = "app.messaging.enabled", havingValue = "true", matchIfMissing = true)
public class ProdutoEventMessagePublisher {

    private static final Logger log = LoggerFactory.getLogger(ProdutoEventMessagePublisher.class);

    private final RabbitTemplate rabbitTemplate;
    private final boolean enabled;
    private final String exchange;
    private final String routingKey;

    public ProdutoEventMessagePublisher(
            RabbitTemplate rabbitTemplate,
            @Value("${app.messaging.enabled:true}") boolean enabled,
            @Value("${app.messaging.exchange:produto.exchange}") String exchange,
            @Value("${app.messaging.routing-key:produto.evento}") String routingKey) {
        this.rabbitTemplate = rabbitTemplate;
        this.enabled = enabled;
        this.exchange = exchange;
        this.routingKey = routingKey;
    }

    /**
     * So envia para o RabbitMQ depois que a transacao do CRUD foi confirmada.
     * O RabbitTemplate usa o JacksonJsonMessageConverter configurado no projeto
     * para transformar o evento em JSON.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publicar(ProdutoAlteradoEvent event) {
        if (!enabled) {
            log.debug("Mensageria desabilitada. Evento nao enviado: {}", event);
            return;
        }

        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, event);
            log.info("Evento de produto publicado no RabbitMQ: operacao={}, produtoId={}, qtd={}",
                    event.operacao(), event.produtoId(), event.quantidade());
        } catch (RuntimeException ex) {
            // A venda/atualizacao ja foi confirmada no banco. O erro de mensageria
            // nao deve desfazer a operacao, mas fica registrado no log.
            log.error("Falha ao publicar evento do produto {} no RabbitMQ.", event.produtoId(), ex);
        }
    }
}
