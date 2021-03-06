import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.OutputStream;
import java.io.InputStream;

/*
 this file is combination of all classes of java client :
 will separate the classes to it's own class file */


class UtilData {
	public static String IP = "127.0.0.1";
	public static int PORT = 8080;
	public static String GETLOCALTIME = "GETLOCALTIME";
}



public class ClientCall{

	// this class is TEST.java file content

	public static void main(String[] args){
		GetLocalTime obj = new GetLocalTime();
		obj.valid.setValue("true");
		obj.execute(UtilData.IP,UtilData.PORT);
		int t = obj.localTime.getValue();

		System.out.println(t);

	}


}





// move this class to GetLocalTime.java
class GetLocalTime{
	
	c_int localTime;
	c_char valid;

	public GetLocalTime(){
		System.out.println("test***********");
		this.localTime = new c_int();
		this.valid = new c_char();


	}

	public int execute(String Ip ,int port){
		// to execute socket connection
		

		// create buffer

		int lenghtMess = localTime.getSize()+valid.getSize();
		byte[] commandBuffer = new byte[100 + 4 + lenghtMess];
		
		for (int i = 0 ; i < commandBuffer.length ; i ++){
			commandBuffer[i] = (byte)' ';
		}


		String commandString = UtilData.GETLOCALTIME;

		for(int i = 0 ; i < commandString.length() ; i ++){
			commandBuffer[i] = (byte)commandString.charAt(i); // buff [0,99] : inserting command string
		}

		int offset = 100;
		String lengthMessString = Integer.toString(lenghtMess);//new String(lenghtMess);

		for(int i = offset,j =0; i < offset+lengthMessString.length() ; i ++){
			commandBuffer[i] = (byte)lengthMessString.charAt(j++); // buff [100,104] : inserting command len
		}

		offset = 104;
		String timeSizeMessage = Integer.toString(localTime.getSize());
		

		for(int i = offset ,j =0; i < offset+timeSizeMessage.length(); i ++){
			commandBuffer[i] = (byte)timeSizeMessage.charAt(j++); // buff [104,] : time
		}
		offset = 104+timeSizeMessage.length();
		String validSizeMessage = Integer.toString(valid.getSize());
		for(int i = offset,j=0 ; i < offset+validSizeMessage.length() ; i ++){
			commandBuffer[i] = (byte)validSizeMessage.charAt(j++); // buff [104,] : time len
		}




		System.out.println(new String(commandBuffer));     //debug


		// end buffer


		// call command
		String timeCommand = "GETLOCALTIME";
		SocketClient(UtilData.IP,UtilData.PORT,commandBuffer);

		// end of call
		



		byte[] timeFromC = new String("time").getBytes();
		this.localTime.setValue(timeFromC);
		
		int timeInInt = this.localTime.getValue();

		return timeInInt;

	}


	  private Socket socket = null;
	  private DataInputStream input = null;
	  private OutputStream output = null;



	  //parametrized constructor for CilentSideProgram
	  public void SocketClient(String address, Integer port,byte[] commandString) {

	    //code to establish a connection
	    try {
	      socket = new Socket(address, port);


	      // sends output to the socket
	      output = socket.getOutputStream();
	    } catch (UnknownHostException e) {
	      e.printStackTrace();
	    } catch (IOException e) {
	      e.printStackTrace();
	    }

		//below lines are used to read message from input
		try {
		   
		    int lenOfCommandString = commandString.length;
		    System.out.println("len  = "+lenOfCommandString);
		    System.out.println(new String(commandString,"UTF-8"));
		    System.out.println(commandString);
		    output.write(commandString, 0, lenOfCommandString);
		    output.flush();
		} catch (IOException e) {
		    e.printStackTrace();
		}

		boolean end = false;
		byte[] messageByte = new byte[200];
	    try 
	    {
	        DataInputStream in = new DataInputStream(socket.getInputStream());

	        int lenofmessage = 113;
	        int cnt = 0;
	        String dataString = "";
	        while(!end)
	        {
	            int bytesRead = in.read(messageByte);
	            dataString += new String(messageByte, 0, bytesRead);
	            if (dataString.length() >= lenofmessage)
	            {

	                end = true; 
		        	System.out.println("MESSAGE LOCAL TIME SERVER : |" + dataString+"|"+dataString.length() );
		        	break;
	            	     
	            }
	            cnt ++;
	        
	        }
	        System.out.println("MESSAGE: lenofmessage 1|" + dataString+"|");

		} catch (IOException e) {
		    e.printStackTrace();
		}



	    //below code to close the connection
	    try {
	      input.close();
	      output.close();
	      socket.close();
	    } catch (IOException e) {
	      e.printStackTrace();
	    }

	  }

}



// move to c_int.java file
class c_int{
	byte[] buf = new byte[5];

	public void setValue(byte[] b){
		this.buf = b;
	}


	public int getValue(){
		// make buf arry to int
		//return this.buf -> int;

		int returnVlaue = 2;

		return returnVlaue;
	}

	public int getSize(){
		return buf.length;	
	}



}


// move to file c_char.java file
class c_char{
	char[] valid;

	public void setValue(String valid){


		this.valid = valid.toCharArray();
	}

	public char[] getValue(){

		return this.valid;
	}

	public int getSize(){
		return valid.length;
	}
}





