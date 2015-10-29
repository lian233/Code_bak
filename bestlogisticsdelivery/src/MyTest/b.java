package MyTest;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.wofu.common.tools.util.DOMHelper;
import com.wofu.common.tools.util.JException;

public class b {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		System.out.println("aaaaaaa");
		Document outStockStatusDoc = DOMHelper.newDocument("<orderCode>aaa</orderCode>", "GBK");
		Element outStockStatusele = outStockStatusDoc.getDocumentElement();	
		aa(outStockStatusele);

	}



	private static void aa(Element outStockStatusele) {
		System.out.println("0");
		Element packages=(Element) outStockStatusele.getElementsByTagName("packages").item(0);
		System.out.println(packages);
		if(packages!=null){
			System.out.println("kaiwanxiao");
		
		NodeList packageList1=packages.getElementsByTagName("package");
		System.out.println("2");
		System.out.println(packages);
		System.out.println(packageList1);
		System.out.println(packageList1.getLength());
		}
		System.out.println("aaaaaaa");
		
	}

}
