package webservermultithread;

import java.io.* ;
import java.net.* ;
import java.util.* ;

public final class Request implements Runnable{
    //Variáveis da classe Request
    //final static String CRLF = "\r\n";
    Socket socketConection;
    //Construtor da classe Request
    public Request(Socket socket) throws Exception
    {
        socketConection = socket;
    }
    
    //Método run() da interface Runnable
    public void run()
    {
        try{
            processRequest();
        }catch (Exception e) {
            System.out.println(e);
        }
    }
    
    private void processRequest() throws Exception
    {
        //Define Input e Output pelos métodos já implementados da classe socket
        InputStream input = socketConection.getInputStream();
        DataOutputStream output = new DataOutputStream(socketConection.getOutputStream());
        
        //Cria instancia de um BufferedReader para ler o input, e assim ler
        //a requisicao HTTP e exibi-la
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String requestText = reader.readLine();
        System.out.println("\n"+requestText);
        
        //Para o restante do header da requisicao, cria-se outro variavel para
        //armazena-la e então exibi-la
        String headerText;
        while ((headerText = reader.readLine()).length() != 0) {
            System.out.println(headerText);
        }
            
        //Tendo encerrado a leitura da requisição, iremos encerrar o socket,
        //reader e input.
        //input.close();
        //reader.close();
        //socketConection.close();

        //Como o texto da linha de requisicao foi salva, iremos tirar dela a
        //informação de qual objeto foi requisitado para ser enviado. Como
        //sabemos que a primeira  palavra é o GET iremos pula-lo.
        StringTokenizer token = new StringTokenizer(requestText);
        token.nextToken(); 
        String object = token.nextToken();
        
        // Acrescenta-se um “.” para que a requisição do arquivo
        // seja feita dentro do diretório do projeto.
        object = "." + object;
        
        if(object.equals("./"))
            object = "./index.html";
        
        System.out.println("\n"+object);
        
        // Para abrir o arquivo criamos um FileInputStream
        // e para posteriores checkagem de se o arquivo
        // foi encontrado ou não e assim montar a resposta
        FileInputStream objectInput = null;
        Boolean objectExists = true;
        
        try{
            objectInput = new FileInputStream(object);
        }catch (FileNotFoundException e) {
            objectExists = false;
            System.out.println("\nDoesn't Exist\n");
        }
          
        // De acordo com a variável previamente criada, checka-se se
        // o arquivo requisitado existe, e então cria-se a mensagem
        // de resposta, e envia-la, assim como o corpo da mensagem.
        String anwserMessage = null;
        if (objectExists) {
                anwserMessage = "HTTP/1.0 200 OK\r\nContent-type: " 
                            +contentType(object) + "\r\n";
                output.writeBytes(anwserMessage+"\r\n");
                sendBytes(objectInput, output);
                objectInput.close();
        } else {
                anwserMessage = "HTTP/1.0 404 Not Found\r\nContent-Type: text/html\r\n" +
                            "<HTML><Head><Title>Not Found</Title></Head>"
                            + "<body><b>Not Found</b></body></HTML>";
                output.writeBytes(anwserMessage+"\r\n");
	}
        
    input.close();
    output.close();
    reader.close();
    socketConection.close();
}
    
    
    private static void sendBytes(FileInputStream objectInput, OutputStream objectOutput) throws Exception
    {
        // Construir um buffer e copia o arquivo para ser enviado pela saida
        // so socket
        byte[] buffer = new byte[1024];
        int bytes = 0;
        while((bytes = objectInput.read(buffer)) != -1 ) {
            objectOutput.write(buffer, 0, bytes);
        }
    }

    private static String contentType(String object)
    {
        if(object.endsWith(".htm") || object.endsWith(".html")) {
            return "text/html";
        }else if(object.endsWith(".jpeg") || object.endsWith(".jpg")){
                return "image/jpeg";
        }else if(object.endsWith(".gif")) {
                return "image/gif";
        }
        return "application/octet-stream";
    }

}
