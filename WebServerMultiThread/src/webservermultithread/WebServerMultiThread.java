package webservermultithread;

/*
 * @author Caio Eduardo Cunha Machado Caetano
 * Matricula: 98090
 * Graduando em Engenharia Eletrica com ênfase em Engenharia de Computação
 * @desc Trabalho 1 pela disciplina de Redes de Computadores
 */

import java.net.* ;

public final class WebServerMultiThread
{
    public static void main(String arvg[]) throws Exception
    {
        //O servidor usará uma porta diferente da padrão (80).
        //portanto, é preciso defini-la, no meu caso usarei um
        //número randomico entre 1025 e 9000
        int porta;
        //porta = 1025+(int)(7975*Math.random());
        
        porta=1100;
        
        //Imprime a porta
        System.out.println(porta+"\n");
        
        //Com a porta já definida, é preciso definir um socket
        //para receber as solicitações
        ServerSocket listenerSocket = new ServerSocket(porta);

        //Como o número de requisições que serão enviadas não é
        //determinado, e estamos tratando de um ervido multithread
        //se faz necessário um laço infinito para processar todas as
        //requisições de serviço HTTP.
        while(true){
            //Neste laço, será recebida a requisição de conexão TCP,
            //e em seguida, para tratar a mesma, teremos uma classe 
            //Request. Para cada nova requisição, será iniciada uma
            //thread da classe Request
            Socket conection = listenerSocket.accept(); //!!!!!!
            Request requisicao = new Request(conection); //!!!!!!!!!!
            Thread thread = new Thread(requisicao);
            thread.start();
        }
    }
}