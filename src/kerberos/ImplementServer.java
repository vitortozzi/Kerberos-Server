/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kerberos;

import java.io.IOException;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.NoSuchPaddingException;
import message.ServerTicket;
import utils.FileUtils;
import utils.TimeUtils;

/**
 *
 * @author Vitor Tozzi
 */
public class ImplementServer extends UnicastRemoteObject implements InterfaceServer{

    String senhaServer = "server12";
    
    public ImplementServer() throws RemoteException{
        super();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws RemoteException {
        int port = 9696;
        String thisAddress;
        try {
            thisAddress = (InetAddress.getLocalHost()).toString();
        } catch (Exception e) {
            throw new RemoteException("Não foi possível pegar o endereço.");
        }

        System.out.println("Endereço IP:" + thisAddress + " ---- Porta: " + port);
        try {
            // Cria o registro
            Registry registry = LocateRegistry.createRegistry(port);
            // Instancia o objeto das implementações do servidor
            ImplementServer implementServer = new ImplementServer();
            // Liga o servidor a TAG, para que o cliente possa encontra-lo
            registry.bind("HelloServer", implementServer);
            /**
             * Inicia banco de dados com senha dos usuários
             */
            
        } catch (Exception e) {
            System.out.println("Erro " + e.getMessage());
        }
    }

    @Override
    public String sayHello() throws RemoteException {
        return "O Servidor retornou oi =}";
    }

    @Override
    public boolean authenticate(String filepath) throws RemoteException {
        
        FileUtils fileUtils;
        try {
            fileUtils = new FileUtils(senhaServer);
            ServerTicket serverTicket = (ServerTicket) fileUtils.readEncryptedObject(filepath);
            System.out.println("**Passo 5: Servidor analisa o ticket do cliente");
            serverTicket.print();
            
            Date date = TimeUtils.getDate();
            Date limit = serverTicket.timestamp;
            if(limit.before(date)){
                return false;
            }
            else{
               String clientID = serverTicket.clientID;
               String serviceID = serverTicket.serviceID;
               if(checkUserAndService(clientID, serviceID)){
                   return true;
               }
            }
            
        } catch (InvalidKeyException ex) {
            Logger.getLogger(ImplementServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(ImplementServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeySpecException ex) {
            Logger.getLogger(ImplementServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(ImplementServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ImplementServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ImplementServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
        return false;
    }
    
    boolean checkUserAndService(String clientID, String serviceID){
        
        List<String> usuarios = new ArrayList<>();
        List<String> servicos = new ArrayList<>();
        
        usuarios.add("cliente");
        usuarios.add("beltrano");
        
        servicos.add("servidor");
        servicos.add("autenticacao");
        
        if(usuarios.contains(clientID) && servicos.contains(serviceID)){
            return true;
        }
        return false;
    }
   
}
