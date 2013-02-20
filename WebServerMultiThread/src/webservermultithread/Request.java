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
        
        // Acrescente um “.” de modo que a requisição do arquivo esteja dentro do diretório atual.
        object = "." + object;
        
        if(object.equals("./"))
            object = "./index.html";
        
        System.out.println("\n"+object);
        
        // Abrir o arquivo requisitado.
        FileInputStream objectInput = null;
        Boolean objectExists = true;
        
        try{
            objectInput = new FileInputStream(object);
        }catch (FileNotFoundException e) {
            objectExists = false;
            System.out.println("\nDoesn't Exist\n");
        }
        
        // Construir a mensagem de resposta.
        String statusLine = null;
        String contentTypeLine = null;
        String entityBody = null;
        if (objectExists) {
                statusLine = "HTTP/1.0 200 OK\r\n";
                contentTypeLine = "Content-type: " + 
                contentType(object) + "\r\n";
        } else {
                statusLine = "HTTP/1.0 404 Not Found\r\n";
                contentTypeLine = "Content-Type: text/html\r\n";
                entityBody = "<HTML><Head><Title>Not Found</Title></Head>"
                             + "<body><b>Not Found</b></body></HTML>";
	}
        
        // Enviar a linha de status.
        output.writeBytes(statusLine);
        // Enviar a linha de tipo de conteúdo.
        output.writeBytes(contentTypeLine);
        // Enviar uma linha em branco para indicar o fim das linhas de cabeçalho.
        output.writeBytes("\r\n");

    // Enviar o corpo da entidade.
    if (objectExists) {
            sendBytes(objectInput, output);
            objectInput.close();
    } else {
            output.writeBytes(entityBody);
    }
    output.close();
    reader.close();
    socketConection.close();
}
    
    
    private static void sendBytes(FileInputStream fis, OutputStream os) throws Exception
    {
        // Construir um buffer de 1K para comportar os bytes no caminho para o socket.
        byte[] buffer = new byte[1024];
        int bytes = 0;
        // Copiar o arquivo requisitado dentro da cadeia de saída do socket.
        while((bytes = fis.read(buffer)) != -1 ) {
            os.write(buffer, 0, bytes);
        }
    }

    private static String contentType(String fileName)
    {
        System.out.println("\nTrying Content Type\n\n\n");
        if(fileName.endsWith(".htm") || fileName.endsWith(".html")) {
            return "text/html";
        }else if(fileName.endsWith(".jpeg") || fileName.endsWith(".jpg")){
                return "image/jpeg";
        }else if(fileName.endsWith(".gif")) {
                return "image/gif";
        }
        return "application/octet-stream";
    }

}
