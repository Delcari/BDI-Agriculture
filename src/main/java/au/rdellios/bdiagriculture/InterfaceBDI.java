package au.rdellios.bdiagriculture;

import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.*;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.annotation.OnEnd;
import jadex.bridge.service.annotation.OnStart;
import jadex.commons.future.IFuture;
import jadex.commons.gui.SGUI;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Int;
import jadex.micro.annotation.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


@Agent(type= BDIAgentFactory.TYPE)
@RequiredServices(@RequiredService(name="scoutBoundary", type=IScoutBoundary.class, scope=ServiceScope.PLATFORM))
@Goals(@Goal(clazz= ScoutBoundary.class))
@Plans(@Plan(trigger=@Trigger(goals= ScoutBoundary.class), body=@Body(service=@ServicePlan(name="scoutBoundary"))))
public class InterfaceBDI
{

    protected JFrame f;
    protected JPanel pnl;
    @Agent
    protected IInternalAccess agent;

    @AgentFeature
    protected IExecutionFeature execFeature;

    @AgentFeature
    protected IBDIAgentFeature bdiFeature;

    @OnStart
    public void body()
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                //Draw the Interface
                f = new JFrame();
                JPanel pnl = new JPanel();
                f.add(pnl);
                GridBagLayout gbl = new GridBagLayout();
                pnl.setLayout(gbl);

                GridBagConstraints gbc = new GridBagConstraints();
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.gridwidth = 2;
                JLabel start = new JLabel("Start : X, Y");
                pnl.add(start, gbc);

                gbc = new GridBagConstraints();
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.gridx = 0;
                gbc.gridy = 1;
                gbc.weightx = 1;
                final JTextField tfX1 = new JTextField("");
                pnl.add(tfX1, gbc);

                gbc = new GridBagConstraints();
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.gridx = 1;
                gbc.gridy = 1;
                gbc.weightx = 1;
                final JTextField tfY1 = new JTextField("");
                pnl.add(tfY1, gbc);

                gbc = new GridBagConstraints();
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.gridx = 0;
                gbc.gridy = 2;
                gbc.gridwidth = 2;
                JLabel end = new JLabel("End : X, Y");
                pnl.add(end, gbc);

                gbc = new GridBagConstraints();
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.gridx = 0;
                gbc.gridy = 3;
                gbc.weightx = 1;
                final JTextField tfX2 = new JTextField("");
                pnl.add(tfX2, gbc);

                gbc = new GridBagConstraints();
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.gridx = 1;
                gbc.gridy = 3;
                gbc.weightx = 1;
                final JTextField tfY2 = new JTextField("");
                pnl.add(tfY2, gbc);

                gbc = new GridBagConstraints();
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.gridx = 0;
                gbc.gridy = 4;
                gbc.gridwidth = 2;
                JTextArea txtOut = new JTextArea();
                txtOut.setLineWrap(true);
                //remove background
                JScrollPane sp = new JScrollPane(txtOut);
                sp.setBorder(BorderFactory.createEmptyBorder(8,2,8,2));
                pnl.add(sp, gbc);

                gbc = new GridBagConstraints();
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.gridx = 0;
                gbc.gridy = 5;
                gbc.gridwidth = 2;
                JButton bt = new JButton("Scan");
                pnl.add(bt, gbc);


                //Send the goal to the agent
                bt.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        execFeature.scheduleStep(new IComponentStep<Void>() {
                            public IFuture<Void> execute(IInternalAccess ia) {
                                try {
                                    final IVector2 startPos = new Vector2Int(Integer.parseInt(tfX1.getText()), Integer.parseInt(tfY1.getText()));
                                    final IVector2 endPos = new Vector2Int(Integer.parseInt(tfX2.getText()), Integer.parseInt(tfY2.getText()));
                                    txtOut.setText("goalStarted: [(startPos) -> (endPos)] (" + startPos + ") -> (" + endPos + ")");
                                    IVector2[] boundary = {startPos, endPos};
                                    bdiFeature.dispatchTopLevelGoal(new ScoutBoundary(boundary)).get();
                                    SwingUtilities.invokeLater(new Runnable()
                                    {
                                        public void run()
                                        {
                                            txtOut.setText("goalFinished");
                                        }
                                    });
                                } catch (Exception e) {
                                    txtOut.setText("Error: " + e.getMessage());
                                    e.printStackTrace();
                                }
                                return IFuture.DONE;
                            }
                        });
                    }
                });



                pnl.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
                f.setSize(210,200);
                //f.pack();
                f.setLocation(SGUI.calculateMiddlePosition(f));
                f.setVisible(true);

            }
        });
    }

    /**
     *  Cleanup when agent is killed.
     */
    //@AgentKilled
    @OnEnd
    public void	cleanup()
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                if(f!=null)
                {
                    f.dispose();
                }
            }
        });
    }
}