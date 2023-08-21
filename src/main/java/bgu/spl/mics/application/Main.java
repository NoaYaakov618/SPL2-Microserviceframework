package bgu.spl.mics.application;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewok;
import bgu.spl.mics.application.passiveObjects.Ewoks;
import bgu.spl.mics.application.passiveObjects.Input;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jdk.nashorn.internal.parser.JSONParser;


import java.io.*;
import java.util.*;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.concurrent.CountDownLatch;


/**
 * This is the Main class of the application. You should parse the input file,
 * create the different components of the application, and run the system.
 * In the end, you should output a JSON.
 */
public class Main {
	public Main() throws IOException {
	}

	public static void main(String[] args) throws FileNotFoundException {

		Gson gson = new Gson();
		Input input;
		try (Reader reader = new FileReader(args[0])) {
			input = gson.fromJson(reader, Input.class);
		} catch (Exception e) {
			System.out.println("couldn't read the file " + args[0]);
			return;
		}
		Ewoks.getInstance().initialize(input.getEwoks());
		CountDownLatch[] cdl = new CountDownLatch[3];
		List<MicroService> mss = new LinkedList<>();
		cdl[1] = new CountDownLatch(5);
		cdl[2] = new CountDownLatch(input.getAttacks().length);

		mss.add(new LeiaMicroservice(input.getAttacks(), cdl));
		mss.add(new LandoMicroservice(input.getLando(), cdl));
		mss.add(new HanSoloMicroservice(cdl));
		mss.add(new C3POMicroservice(cdl));
		mss.add(new R2D2Microservice(input.getR2D2(),cdl));


		cdl[0] = new CountDownLatch(mss.size() - 1);

		List<Thread> threads = new LinkedList<>();
		for (MicroService m : mss) {
			threads.add(new Thread(m, "my_thread - " + m.getName()));
		}
		for (Thread t : threads) {
			t.start();
		}
		try {
			cdl[1].await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Gson gsonBuilder= new GsonBuilder().setPrettyPrinting().create();
		try(FileWriter writer = new FileWriter("output.json")){
			gsonBuilder.toJson(Diary.getInstance(),writer );
		} catch(IOException ignore){
		}
	}


}

