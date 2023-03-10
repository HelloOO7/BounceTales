package bouncetools;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import xstandard.formats.zip.ZipArchive;
import xstandard.fs.FSFile;
import xstandard.fs.accessors.DiskFile;
import xstandard.gui.DialogUtils;
import xstandard.gui.DnDHelper;
import xstandard.gui.components.ComponentUtils;
import xstandard.gui.file.CommonExtensionFilters;
import xstandard.gui.file.XFileDialog;
import xstandard.thread.ThreadingUtils;

public class ComposerGUI extends javax.swing.JFrame {

	private static final String DEFAULT_DECOMPOSE_DIR = "res_decomposed";

	public ComposerGUI() {
		initComponents();
		setLocationRelativeTo(null);
		DnDHelper.addFileDropTarget(decomposeDropTarget, new DnDHelper.FileDropListener() {
			@Override
			public void acceptDrop(List<File> files) {
				for (File f : files) {
					if (f.getName().endsWith(".jar")) {
						doDecomposeFile(new DiskFile(f));
					}
					break;
				}
			}
		});
		checkSetButtonStatus();
	}

	private FSFile getDecomposeDir() {
		return new DiskFile(DEFAULT_DECOMPOSE_DIR);
	}

	private boolean decomposeDirExists() {
		FSFile dir = getDecomposeDir();
		if (dir.exists()) {
			FSFile versionFile = dir.getChild(ResourceComposer.VERSION_FILENAME);
			if (versionFile.exists()) {
				return new String(versionFile.getBytes(), StandardCharsets.UTF_8).trim().equals(ResourceComposer.VERSION);
			}
		}
		return false;
	}

	private void checkSetButtonStatus() {
		if (decomposeDirExists()) {
			btnCompose.setEnabled(true);
			decomposeDropTarget.setText("Ready.");
		} else {
			btnCompose.setEnabled(false);
		}
	}

	private void doDecomposeFile(FSFile f) {
		if (ZipArchive.isZip(f)) {
			f = new ZipArchive(f);
		}
		final FSFile ff = f;
		decomposeDropTarget.setText("Decomposing...");
		ThreadingUtils.runOnNewThread((() -> {
			try {
				ResourceDecomposer.decompose(ff, getDecomposeDir());
				SwingUtilities.invokeLater((() -> {
					checkSetButtonStatus();
				}));
			} catch (Exception ex) {
				SwingUtilities.invokeLater((() -> {
					DialogUtils.showExceptionTraceDialog(ex);
					decomposeDropTarget.setText("Error.");
				}));
			}
		}));
	}

	private void doCompose() {
		btnCompose.setText("Composing...");
		btnCompose.setEnabled(false);
		ThreadingUtils.runOnNewThread((() -> {
			try {
				ResourceComposer.compose(getDecomposeDir());
				btnCompose.setText("Success!");
				Timer t = new Timer(500, new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						btnCompose.setText("Compose resources!");
						btnCompose.setEnabled(true);
					}
				});
				t.setRepeats(false);
				t.start();
			} catch (IOException ex) {
				DialogUtils.showExceptionTraceDialog(ex);
				btnCompose.setText("Error.");
			}
		}));
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dropPanel = new javax.swing.JPanel();
        decomposeDropTarget = new javax.swing.JLabel();
        btnCompose = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Bounce Composer");
        setResizable(false);

        dropPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        decomposeDropTarget.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        decomposeDropTarget.setText("<html> <center> Drop Bounce Tales JAR<br/> here to decompose.<br/> (or click to select a file) </center> </html>");
        decomposeDropTarget.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                decomposeDropTargetMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout dropPanelLayout = new javax.swing.GroupLayout(dropPanel);
        dropPanel.setLayout(dropPanelLayout);
        dropPanelLayout.setHorizontalGroup(
            dropPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dropPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(decomposeDropTarget, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                .addContainerGap())
        );
        dropPanelLayout.setVerticalGroup(
            dropPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dropPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(decomposeDropTarget, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
                .addContainerGap())
        );

        btnCompose.setText("Compose resources!");
        btnCompose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnComposeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(dropPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCompose, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnCompose, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(dropPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void decomposeDropTargetMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_decomposeDropTargetMouseClicked
		FSFile jarFile = XFileDialog.openFileDialog(CommonExtensionFilters.JAR);
		if (jarFile != null) {
			doDecomposeFile(jarFile);
		}
    }//GEN-LAST:event_decomposeDropTargetMouseClicked

    private void btnComposeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnComposeActionPerformed
		doCompose();
    }//GEN-LAST:event_btnComposeActionPerformed

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		ComponentUtils.setSystemNativeLookAndFeel();

		/* Create and display the form */
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new ComposerGUI().setVisible(true);
			}
		});
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCompose;
    private javax.swing.JLabel decomposeDropTarget;
    private javax.swing.JPanel dropPanel;
    // End of variables declaration//GEN-END:variables
}
