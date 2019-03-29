package chatcliente;



import java.awt.BorderLayout;
import javax.swing.*;
import java.awt.event.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ChatCliente {

	public static void main(String[] args) {
            MarcoCliente mimarco=new MarcoCliente();
            mimarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}

class MarcoCliente extends JFrame{
	
	public MarcoCliente(){
            setBounds(600,300,280,350);
            LaminaMarcoCliente milamina=new LaminaMarcoCliente();
            add(milamina);
            setVisible(true);
            addWindowListener(new EnvioOnline());
	}	
}

//Muestra cuando se conecta un nuevo usuario
class EnvioOnline extends WindowAdapter{
    
    public void windowOpened(WindowEvent e){
        try{
            String ip = JOptionPane.showInputDialog("introduce la IP:");
            int puerto = Integer.parseInt(JOptionPane.showInputDialog("introduce el puerto:"));
            Socket miSocket = new Socket(ip,puerto);
            
            PaqueteEnvio datos = new PaqueteEnvio();
            
            datos.setMensaje("Online");
            
            ObjectOutputStream paqueteDatos = new ObjectOutputStream(miSocket.getOutputStream());
            datos.setIp(ip);
            paqueteDatos.writeObject(datos);

            miSocket.close();
            
        }catch(Exception e2){
            System.out.println(e2.getMessage());
        }
    }
}

class LaminaMarcoCliente extends JPanel implements Runnable{
	
	public LaminaMarcoCliente(){
            String nickUsuario = JOptionPane.showInputDialog("Nick: ");

            JLabel n_nick = new JLabel("Nick: ");

            add(n_nick);

            nick = new JLabel();

            nick.setText(nickUsuario);
            add(nick);

            JLabel texto = new JLabel("CLIENTE");
            add(texto);

            ip = new JTextField(8);  
            add(ip); 

            campochat = new JTextArea(12,20);
            add(campochat);

            campo1 = new JTextField(20); 
            add(campo1);

            miboton = new JButton("Enviar");

            EnviaTexto mievento = new EnviaTexto();
            miboton.addActionListener(mievento);
            add(miboton);

            Thread mihilo = new Thread(this);
            mihilo.start();
	}

	private class EnviaTexto implements ActionListener{

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Socket misocket = new Socket("127.0.0.1",9999);
                    
                    PaqueteEnvio datos = new PaqueteEnvio();
                    
                    datos.setNick(nick.getText());
                    
                    datos.setIp(ip.getText());
                    
                    datos.setMensaje(campo1.getText());
                    
                    ObjectOutputStream  paquete_datos = new ObjectOutputStream(misocket.getOutputStream());
                    
                    paquete_datos.writeObject(datos);
                    
                    misocket.close();                  
                    
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }
            
        }
        
	private JTextField campo1, ip;
        private JLabel nick;
        private JTextArea campochat;
	private JButton miboton; 
        
    //El cliente recibe el paqute de datos a través de un objeto   
    @Override
    public void run() {
        try{
            ServerSocket servidorCliente = new ServerSocket(9090);
            Socket cliente;
            
            PaqueteEnvio paqueteRecibido;
            
            while(true){
                cliente = servidorCliente.accept();
                ObjectInputStream flujoEntrada = new ObjectInputStream(cliente.getInputStream());
                
                paqueteRecibido = (PaqueteEnvio) flujoEntrada.readObject();
                
                campochat.append("\n" + paqueteRecibido.getNick() + ": " + paqueteRecibido.getMensaje());
            }
            
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }    
        
}

class PaqueteEnvio implements Serializable{
    private String nick, ip, mensaje;

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
    
}