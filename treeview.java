import org.w3c.dom.*;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class XMLTreeViewer {

    private JFrame frame;
    private JTree tree;

    public XMLTreeViewer() {
        frame = new JFrame("XML Tree Viewer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        tree = new JTree();
        JScrollPane scrollPane = new JScrollPane(tree);
        frame.add(scrollPane);

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem openItem = new JMenuItem("Open XML File");

        openItem.addActionListener(e -> openXMLFile());
        fileMenu.add(openItem);
        menuBar.add(fileMenu);
        frame.setJMenuBar(menuBar);

        frame.setVisible(true);
    }

    private void openXMLFile() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(frame);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(file);
                document.getDocumentElement().normalize();

                DefaultMutableTreeNode root = parseNode(document.getDocumentElement());
                tree.setModel(new DefaultTreeModel(root));

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error loading XML file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private DefaultMutableTreeNode parseNode(Node node) {
        DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(node.getNodeName());

        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                treeNode.add(parseNode(child));
            } else if (child.getNodeType() == Node.TEXT_NODE) {
                String text = child.getTextContent().trim();
                if (!text.isEmpty()) {
                    treeNode.add(new DefaultMutableTreeNode(text));
                }
            }
        }

        return treeNode;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(XMLTreeViewer::new);
    }
}
