package com.wofu.ecommerce.imagespace;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.border.Border;

public class ExampleAccordion extends JPanel {
    /** *//**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final JPanel panel = new JPanel();
    private final JLabel label = new JLabel();
    // �ָ��
    private final JSplitPane split = new JSplitPane();
    private final JScrollPane scroll;
    // �۵�Ч��
    public ExampleAccordion() {
        super(new BorderLayout());
        panel.setOpaque(true);
        panel.setBackground(new Color(116, 149, 226));
        // ������
        scroll = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(10);
        scroll.getViewport().add(panel);
        // ���������б�
        List panelList = makeList();
        // �趨����
        accordionListener exr = new accordionListener() {
            public void accordionStateChanged(accordionEvent e) {
                initComponent();
            }
        };
        for (Iterator it = panelList.iterator(); it.hasNext();) {
            AccordionPanel epl = (AccordionPanel) it.next();
            addComponent(epl);
            epl.addaccordionListener(exr);
        }
        // ���ع���������
        scroll.getViewport().addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                initComponent();
            }
        });
        // �趨��С
        label.setPreferredSize(new Dimension(200, 260));
        scroll.setPreferredSize(new Dimension(200, 260));
        scroll.setMinimumSize(new Dimension(200, 260));
        split.setLeftComponent(scroll);
        split.setRightComponent(label);
        split.setDividerSize(1);
        split.setBackground(Color.WHITE);
        add(split, BorderLayout.CENTER);
    }
    public void initComponent() {
        Rectangle re = scroll.getViewport().getViewRect();
        Insets ins = panel.getInsets();
        int cw = (int) re.getWidth() - ins.left - ins.right - 20;
        int ch = 10;
        Component[] list = panel.getComponents();
        for (int i = 0; i < list.length; i++) {
            JComponent tmp = (JComponent) list[i];
            int th = tmp.getPreferredSize().height;
            tmp.setPreferredSize(new Dimension(cw, th));
            ch = ch + th + 10;
        }
        panel.setPreferredSize(new Dimension((int) re.getWidth(), ch + ins.top
                + ins.bottom));
        panel.revalidate();
    }
    public void addComponent(Component label) {
        SpringLayout layout = new SpringLayout();
        Component[] list = panel.getComponents();
        if (list.length == 0) {
            layout.putConstraint(SpringLayout.WEST, label, 10,
                    SpringLayout.WEST, panel);
            layout.putConstraint(SpringLayout.NORTH, label, 10,
                    SpringLayout.NORTH, panel);
        } else {
            JComponent cmp = null;
            for (int i = 0; i < list.length; i++) {
                JComponent tmp = (JComponent) list[i];
                layout.putConstraint(SpringLayout.WEST, tmp, 10,
                        SpringLayout.WEST, panel);
                if (cmp == null) {
                    layout.putConstraint(SpringLayout.NORTH, tmp, 10,
                            SpringLayout.NORTH, panel);
                } else {
                    layout.putConstraint(SpringLayout.NORTH, tmp, 10,
                            SpringLayout.SOUTH, cmp);
                }
                cmp = tmp;
            }
            layout.putConstraint(SpringLayout.WEST, label, 10,
                    SpringLayout.WEST, panel);
            layout.putConstraint(SpringLayout.NORTH, label, 10,
                    SpringLayout.SOUTH, cmp);
        }
        panel.add(label);
        panel.setLayout(layout);
        initComponent();
    }
    private List makeList() {
        List panelList = new ArrayList();
        panelList.add(new AccordionPanel("�б�1") {
            /** *//**
             * 
             */
            private static final long serialVersionUID = 1L;
            public JPanel makePanel() {
                JPanel pnl = new JPanel(new GridLayout(0, 1));
                JCheckBox c1 = new JCheckBox("aaaaaa");
                JCheckBox c2 = new JCheckBox("bbbbbb");
                c1.setOpaque(false);
                c2.setOpaque(false);
                pnl.add(c1);
                pnl.add(c2);
                pnl.setSize(new Dimension(0, 60));
                pnl.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
                return pnl;
            }
        });
        panelList.add(new AccordionPanel("�б�2") {
            /** *//**
             * 
             */
            private static final long serialVersionUID = 1L;
            public JPanel makePanel() {
                JPanel pnl = new JPanel(new GridLayout(0, 1));
                pnl.add(new JLabel("���������һ��"));
                pnl.add(new JLabel("�ɸ�����������"));
                pnl.add(new JLabel("ɽ�������Ʈ��"));
                pnl.add(new JLabel("�����������Ƽ"));
                pnl.setSize(new Dimension(0, 100));
                pnl.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
                return pnl;
            }
        });
        panelList.add(new AccordionPanel("�б�3") {
            /** *//**
             * 
             */
            private static final long serialVersionUID = 1L;
            public JPanel makePanel() {
                JPanel pnl = new JPanel(new GridLayout(0, 1));
                JRadioButton b1 = new JRadioButton("aa");
                JRadioButton b2 = new JRadioButton("bb");
                JRadioButton b3 = new JRadioButton("cc");
                b1.setOpaque(false);
                b2.setOpaque(false);
                b3.setOpaque(false);
                pnl.add(b1);
                pnl.add(b2);
                pnl.add(b3);
                ButtonGroup bg = new ButtonGroup();
                bg.add(b1);
                bg.add(b2);
                bg.add(b3);
                b1.setSelected(true);
                pnl.setSize(new Dimension(0, 80));
                pnl.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
                return pnl;
            }
        });
        return panelList;
    }
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                createUI();
            }
        });
    }
    public static void createUI() {
        JFrame frame = new JFrame("JAVAʵ����Windows������");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(new ExampleAccordion());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
class accordionEvent extends java.util.EventObject {
    /** *//**
     * 
     */
    private static final long serialVersionUID = 1L;
    public accordionEvent(Object source) {
        super(source);
    }
}
interface accordionListener {
    public void accordionStateChanged(accordionEvent e);
}
abstract class AccordionPanel extends JPanel {
    abstract public JPanel makePanel();
    private final String _title;
    private final JLabel label;
    private final JPanel panel;
    private boolean openFlag = false;
    public AccordionPanel(String title) {
        super(new BorderLayout());
        _title = title;
        label = new JLabel("�� " + title) {
            /** *//**
             * 
             */
            private static final long serialVersionUID = 1L;
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                // ���ƽ���
                g2.setPaint(new GradientPaint(50, 0, Color.WHITE, getWidth(),
                        getHeight(), new Color(199, 212, 247)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        label.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                openFlag = !openFlag;
                initPanel();
                fireaccordionEvent();
            }
        });
        label.setForeground(new Color(33, 93, 198));
        label.setFont(new Font("����", 1, 12));
        label.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 2));
        panel = makePanel();
        panel.setOpaque(true);
        Border outBorder = BorderFactory.createMatteBorder(0, 2, 2, 2,
                Color.WHITE);
        Border inBorder = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        Border border = BorderFactory.createCompoundBorder(outBorder, inBorder);
        panel.setBorder(border);
        panel.setBackground(new Color(240, 240, 255));
        add(label, BorderLayout.NORTH);
    }
    public boolean isSelected() {
        return openFlag;
    }
    public void setSelected(boolean flg) {
        openFlag = flg;
        initPanel();
    }
    protected void initPanel() {
        if (isSelected()) {
            label.setText("�� " + _title);
            add(panel, BorderLayout.CENTER);
            setPreferredSize(new Dimension(getSize().width,
                    label.getSize().height + panel.getSize().height));
        } else {
            label.setText("�� " + _title);
            remove(panel);
            setPreferredSize(new Dimension(getSize().width,
                    label.getSize().height));
        }
        revalidate();
    }
    protected ArrayList accordionListenerList = new ArrayList();
    public void addaccordionListener(accordionListener listener) {
        if (!accordionListenerList.contains(listener))
            accordionListenerList.add(listener);
    }
    public void removeaccordionListener(accordionListener listener) {
        accordionListenerList.remove(listener);
    }
    public void fireaccordionEvent() {
        List list = (List) accordionListenerList.clone();
        Iterator it = list.iterator();
        accordionEvent e = new accordionEvent(this);
        while (it.hasNext()) {
            accordionListener listener = (accordionListener) it.next();
            listener.accordionStateChanged(e);
        }
    }
}