
package chatcliente;


import javax.swing.*;

import java.awt.*;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import chatcliente.ChatCliente;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;

public class ChatServidor  {
    
	public static void main(String[] args) {
            MarcoServidor mimarco=new MarcoServidor();
            mimarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
	}	
}

class MarcoServidor extends JFrame implements Runnable{
	
	public MarcoServidor(){
            setBounds(1200,300,280,350);				
            JPanel milamina= new JPanel();
            milamina.setLayout(new BorderLayout());
            areatexto=new JTextArea();
            milamina.add(areatexto,BorderLayout.CENTER);
            add(milamina);
            setVisible(true);
            Thread mihilo = new Thread(this);
            mihilo.start();
        }

    //Se pone el servidor a la escucha del cliente    
    @Override
    public void run() {
        System.out.println("Estoy a la escucha");
        
            try {
                ServerSocket servidor = new ServerSocket(9999);
                String nick, ip, mensaje;
                PaqueteEnvio paquete_recibido;
                
                while(true){
                    Socket misocket = servidor.accept();
                    
                    //Recibe los datos     
                    ObjectInputStream paquete_datos = new ObjectInputStream(misocket.getInputStream());
                    paquete_recibido = (PaqueteEnvio) paquete_datos.readObject();
             
                    nick = paquete_recibido.getNick();
                    ip = paquete_recibido.getIp();
                    mensaje = paquete_recibido.getMensaje();
                    System.out.println("usuario "+nick +" conectado");

                    if(!mensaje.equals("Online")){
                        
                        areatexto.append("\n" + nick +": "+ mensaje);
                        Socket enviaDestinatario = new Socket(ip,9090);

                        ObjectOutputStream paqueteReenvio = new ObjectOutputStream(enviaDestinatario.getOutputStream());

                        paqueteReenvio.writeObject(paquete_recibido);

                        enviaDestinatario.close();

                        misocket.close();
                    
                    }else{
                        //Detecta Online

                        InetAddress localizacion = misocket.getInetAddress();
                        
                        //String puerto = localizacion.getAddress().toString();
                        String ipRemota = localizacion.getHostAddress();

                        areatexto.append("Online: "+nick);
                        
                        areatexto.append("\n" + nick +" conectado, ip: "+ipRemota);
                    }
                }
                
            } catch (IOException ex) {
                ex.getMessage();
            } catch (ClassNotFoundException ex) {
                ex.getMessage();
            }
    }
    
    private JTextArea areatexto;
}
