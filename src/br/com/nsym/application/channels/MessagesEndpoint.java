package br.com.nsym.application.channels;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 * Endpoint para fazer o push das notificacoes de mensangens do sistema
 *
 * @author Ibrahim Yousef quatani
 *
 * @version 2.0.0
 * @since 1.0.0, 25/10/2016
 */
@ServerEndpoint("/channels/messages")
public class MessagesEndpoint {

    @Inject
    @Default
    private WebSocketSessions sessions;

    /**
     * Quando uma sessao abrir, adiciona na lista
     *
     * @param session a sessao que se abre
     */
    @OnOpen
    public void onOpenSession(Session session) {
        this.sessions.add(session);
    }

    /**
     * Quando uma sessao se encerrar, remove da lista
     *
     * @param session a sessao que se encerra
     */
    @OnClose
    public void onCloseSession(Session session) {
        this.sessions.remove(session);
    }
}
