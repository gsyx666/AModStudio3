package mbpcm.ui;
/*
 * Copyright (c) 2011 Karl Tauber <karl at jformdesigner dot com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  o Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;

/**
 * A demo for JSplitPaneWithZeroSizeDividerDemo.
 */
public class JSplitPaneWithZeroSizeDividerDemo
{
	public static void main( String[] args ) {
		try {
			UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
		} catch( Exception ex ) {
			ex.printStackTrace();
		}

		// create left and right components
		JComponent left = new JScrollPane( new JTree() );
		JComponent right = new JScrollPane( new JList(
			new String[] { "white", "black", "gray", "red", "green", "blue" } ) );

		// remove borders from scroll panes
		left.setBorder( null );
		right.setBorder( null );

		// create split pane
		ManojUI.ZSplitPane splitPane = new ManojUI.ZSplitPane();
		splitPane.setBorder( null );
		splitPane.setDividerLocation( 200 );
		splitPane.setLeftComponent( left );
		splitPane.setRightComponent( right );

		// create frame
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.add( splitPane );
		frame.setSize( 400, 300 );
		frame.setVisible( true );
	}
}
