package uk.ac.open.kmi.watson.services.test;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;

public class IndexesInspection extends JFrame implements ActionListener {

    protected String luceneDocumentIndex = "/Users/mda99/Desktop/Lucene-indexes/documents3/";
	protected String luceneEntityIndex =   "/Users/mda99/Desktop/Lucene-indexes/entities3/";
	
	private boolean entities = false;
	
	private JTextField query = new JTextField(80);
	private JLabel label = new JLabel("query: ");
	private JButton OKButton = new JButton("OK");
	private JTextArea textArea = new JTextArea (90, 20);
	
	public IndexesInspection(boolean ents) {
		entities = ents;
		if (ents) setTitle("Query Watson Lucene Index: Entities");
		else setTitle("Query Watson Lucene Index: Documents");
		
		JPanel panel = new JPanel(new BorderLayout());
		JPanel qpanel = new JPanel(); 
		qpanel.add(label);
		qpanel.add(query);
		OKButton.addActionListener(this);
		qpanel.add(OKButton);
		panel.add(qpanel, BorderLayout.NORTH);
		panel.add(textArea, BorderLayout.CENTER);
		
		this.getContentPane().add(panel);
		this.pack(); this.setVisible(true);
	}

	public static void main(String[] args){
		IndexesInspection ii = new IndexesInspection(true);
	}

	public void actionPerformed(ActionEvent arg0) {
		IndexSearcher indexSearcher = null;
		try {
		if (entities){
			indexSearcher= new IndexSearcher(luceneEntityIndex);			
		}
		else {
			indexSearcher= new IndexSearcher(luceneDocumentIndex);	
		}
		} catch (IOException e) {
			e.printStackTrace(); textArea.setText("Error doc");
		}
		Analyzer language = new StandardAnalyzer();
		QueryParser qp = new QueryParser("uri", language);
		org.apache.lucene.search.Query q;
		Hits results = null;
		try {
			System.out.println(query.getText());
			q = qp.parse(query.getText());
			results = indexSearcher.search(q);
		} catch (Exception e) {
			e.printStackTrace(); textArea.setText("Error query"); return;
		}
		if (results.length() == 0 ) {textArea.setText("No Result"); return;}
		Document doc = null; textArea.setText("");
		for (int i = 0 ; i < results.length(); i++){
		try {
			doc = results.doc(i);
		} catch (IOException e1) {
			e1.printStackTrace(); textArea.setText("Error results"); return;
		}
		
		Enumeration enu = doc.fields();
		while(enu.hasMoreElements()){
			Field f = (Field)enu.nextElement();
			textArea.append("* "+ f.name()+": ");
			textArea.append(doc.get(f.name())+"\n");
		}
		textArea.append("\n");
		}
		try {
			indexSearcher.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
