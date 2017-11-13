package com.emmanuelmess.serialio

import java.awt.*
import javax.swing.*
import javax.swing.border.EmptyBorder

class ChatGUI(appName: String, val send:(String) -> Unit) {

    private var firstFrame = JFrame("Puertos [COMx]")
    private var newFrame = JFrame(appName)
    private var sendMessage =  JButton("Send Message")
    private var messageBox = JTextField(30)
    private var chatBox =  JTextArea()

    fun preDisplay(ended: (baudrateIndex: Int, databitsIndex: Int, stopbitsIndex: Int, parityIndex: Int,
                                  com1: String, com2: String) -> Unit) {
        val mainPanel = JPanel()
        mainPanel.layout = BorderLayout()

        val lCom1 = JLabel("Puerto para enviar:")
        val lCom2 = JLabel("Puerto a escuchar:")
        val lBaudrates = JLabel("Baudaje:")
        val lBits = JLabel("Bits:")
        val lStopbits = JLabel("Bits de parada:")
        val lParity = JLabel("Paridad:")
        val com1 = JTextField("COM1", 5)
        val com2 = JTextField("COM2", 5)

        val baudratesCombo = JComboBox<String>(Main.BAUDRATES_NAMES.toTypedArray())
        baudratesCombo.selectedIndex = 5
        val bitsCombo = JComboBox<String>(Main.BITS_NAMES.toTypedArray())
        bitsCombo.selectedIndex = 3
        val stopbitsCombo = JComboBox<String>(Main.STOPBITS_NAMES.toTypedArray())
        stopbitsCombo.selectedIndex = 0
        val parityCombo = JComboBox<String>(Main.PARITY_NAMES.toTypedArray())
        parityCombo.selectedIndex = 0

        val center = JPanel()
        center.layout = GridLayout(6, 2, 5, 5)
        center.add(lCom1)
        center.add(com2)
        center.add(lCom2)
        center.add(com1)

        center.add(lBaudrates)
        center.add(baudratesCombo)
        center.add(lBits)
        center.add(bitsCombo)
        center.add(lStopbits)
        center.add(stopbitsCombo)
        center.add(lParity)
        center.add(parityCombo)

        center.border = EmptyBorder(5, 5, 5, 5)

        val start = JButton("Iniciar")
        start.addActionListener {
            firstFrame.dispose()
            ended(baudratesCombo.selectedIndex, bitsCombo.selectedIndex,
                    stopbitsCombo.selectedIndex, parityCombo.selectedIndex,
                    com1.text, com2.text)
        }

        mainPanel.add(center, BorderLayout.CENTER)
        mainPanel.add(start, BorderLayout.SOUTH)
        mainPanel.border = EmptyBorder(5, 5, 5, 5)

        firstFrame.add(mainPanel)
        firstFrame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        firstFrame.pack()
        firstFrame.isVisible = true

        com1.requestFocusInWindow()
    }

    fun display() {
        val mainPanel = JPanel()
        mainPanel.layout = BorderLayout()

        val southPanel = JPanel()
        southPanel.layout = GridBagLayout()

        messageBox.addActionListener {
            newChatMsg()
        }

        sendMessage.addActionListener {
            newChatMsg()
        }

        chatBox.isEditable = false
        chatBox.font = Font("Serif", Font.PLAIN, 15)
        chatBox.lineWrap = true

        mainPanel.add(JScrollPane(chatBox), BorderLayout.CENTER)

        val left = GridBagConstraints()
        left.anchor = GridBagConstraints.LINE_START
        left.fill = GridBagConstraints.HORIZONTAL
        left.weightx = 512.0
        left.weighty = 1.0

        val right = GridBagConstraints()
        right.insets = Insets(0, 10, 0, 0)
        right.anchor = GridBagConstraints.LINE_END
        right.fill = GridBagConstraints.NONE
        right.weightx = 1.0
        right.weighty = 1.0

        southPanel.add(messageBox, left)
        southPanel.add(sendMessage, right)
        southPanel.border = EmptyBorder(5, 5, 5, 5)

        mainPanel.add(BorderLayout.SOUTH, southPanel)
        mainPanel.border = EmptyBorder(5, 5, 5, 5)

        newFrame.add(mainPanel)
        newFrame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        newFrame.setSize(470, 300)
        newFrame.isVisible = true

        messageBox.requestFocusInWindow()
    }

    private fun newChatMsg() {
        if (messageBox.getText().isNotEmpty()) {
            if (messageBox.getText() == "/clear") {
                chatBox.text = ""
                messageBox.text = ""
            } else {
                send(messageBox.text)
                messageBox.text = ""
            }
        }
    }

    fun receive(p: String, msg: String) {
        msg("<$p>  $msg")
    }

    fun error(e: String) {
        msg("ERROR: $e")
    }

    fun msg(e: String) {
        chatBox.append("$e\n")
        messageBox.text = ""
        messageBox.requestFocus()
    }

}
