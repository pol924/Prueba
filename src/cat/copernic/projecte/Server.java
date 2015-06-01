package cat.copernic.projecte;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	private static final int PORT = 39168;
	private static final String FICHERO = "prueba.txt";

	public static  void insertarUsuario(String aInsertar,String fit) throws FileNotFoundException, IOException {
		BufferedWriter out = null; 
		out=new BufferedWriter(new FileWriter(fit, true));
		out.write("\n");
		out.write(aInsertar);
		out.close();
	}


	public static void removeLineFromFile(String file, String lineToRemove) {

		try {

			File inFile = new File(file);

			if (!inFile.isFile()) {
				System.out.println("Parameter is not an existing file");
				return;
			}

			//Construct the new file that will later be renamed to the original filename. 
			File tempFile = new File(inFile.getAbsolutePath() + ".tmp");

			BufferedReader br = new BufferedReader(new FileReader(file));
			PrintWriter pw = new PrintWriter(new FileWriter(tempFile));

			String line = null;

			//Read from the original file and write to the new 
			//unless content matches data to be removed.
			while ((line = br.readLine()) != null) {

				if (!line.trim().equals(lineToRemove)) {
					//System.out.println(line+"\n"+lineToRemove);
					pw.println(line);
					pw.flush();
				}
			}
			pw.close();
			br.close();

			//Delete the original file
			if (!inFile.delete()) {
				System.out.println("Could not delete file");
				return;
			} 

			//Rename the new file to the filename the original file had.
			if (!tempFile.renameTo(inFile))
				System.out.println("Could not rename file");

		}
		catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}




	public static  void modificarUsuario(String aInsertar,String f) throws FileNotFoundException, IOException {
		BufferedWriter out = null; 
		out=new BufferedWriter(new FileWriter(f, true));
		out.write(aInsertar);
		out.close();
	}

	
public static void LoginUser(String user,String pass,Socket client){
		
		BufferedReader br = null;
						
		String mod = null;
		String crear = null;
		String garaje = null;
		String cal = null;
		String aire = null;
		boolean confirm = false;				
		try {		                     
			//archivo = new File (FICHERO);
			//fr = new FileReader (archivo);
			br = new BufferedReader(new FileReader (new File (FICHERO)));
			String [] arr = null;

			String linea = null;
			while((linea=br.readLine())!=null){


				arr = linea.split("=");

				if(arr[1].equals(user)&&arr[2].equals(pass)){

					System.out.println("Hello "+user+"!");
					mod = arr[3];
					crear = arr[4];
					garaje = arr[5];
					cal = arr[6];
					aire = arr[7];
					confirm = true;

					break;
				}else{
					System.out.println("no aceptado");
					confirm = false;
				}



				//ENVIAR

			}

			if(confirm){
				try(PrintWriter writer = new PrintWriter(client.getOutputStream())){
					writer.println(mod);
					writer.flush();
					writer.println(crear);
					writer.flush();
					writer.println(garaje);
					writer.flush();
					writer.println(cal);
					writer.flush();
					writer.println(aire);
					writer.flush();
					String d = String.valueOf(confirm);
					writer.println(d);
					writer.flush();
					//System.out.println(luces);
					System.out.println("USUARIO ENVIADO");

				}catch (Exception e) {
					e.printStackTrace();
				}
			}else{		                    	 
			}

			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}

	
	}
	
	
	
	public static void ModificarUser(String AntiguoUser,String NuevoUser,String fichero,Socket client){
		try {
			removeLineFromFile(fichero, AntiguoUser);
			modificarUsuario(NuevoUser,fichero);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try(PrintWriter writer = new PrintWriter(client.getOutputStream())){
			writer.println("1");
			writer.flush();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	public static void CrearUser(String user,String fichero,Socket client){
		try {
			insertarUsuario(user,fichero);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try(PrintWriter writer = new PrintWriter(client.getOutputStream())){
			writer.println("1");
			writer.flush();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	public static void ConnexionSocket(){
		try {
			ServerSocket sk = new ServerSocket(PORT);
			while (true) {
				System.out.println("Esperando conexion...");
				Socket client = sk.accept();
				System.out.println("Aceptado");



				String opcion = null;
				BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));

				opcion = reader.readLine();


				switch (opcion) {
				case "login":{

					String user = reader.readLine();
					String pass = reader.readLine();
					
					LoginUser(user,pass,client);
															
				}

				break;

				case "modificar":{
					
					String userca = reader.readLine();
					String userm = reader.readLine();
					
					ModificarUser(userca,userm,FICHERO,client);
				}
					break;
				case "crear":{
															
					String userc = reader.readLine();
					
					CrearUser(userc,FICHERO,client);					
				}
					break;
				case "accion":{
					String ac = reader.readLine();					
					System.out.println(ac);
					try(PrintWriter writer = new PrintWriter(client.getOutputStream())){
						writer.println("true");
						writer.flush();
					}catch (Exception e) {
						e.printStackTrace();
					}
					
				}
				default:
					break;
				}

				
				reader.close();
				//fr.close();
				//br.close();
				client.close();
				System.out.println("Se ha ido");
				System.out.println();


			}
			
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	public static void main(String args[]) {
         
		ConnexionSocket();
		
	}
		
}
