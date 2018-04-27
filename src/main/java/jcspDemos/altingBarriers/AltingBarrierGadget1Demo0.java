//////////////////////////////////////////////////////////////////////
//                                                                  //
//  jcspDemos Demonstrations of the JCSP ("CSP for Java") Library   //
//  Copyright (C) 1996-2018 Peter Welch, Paul Austin and Neil Brown //
//                2001-2004 Quickstone Technologies Limited         //
//                2005-2018 Kevin Chalmers                          //
//                                                                  //
//  You may use this work under the terms of either                 //
//  1. The Apache License, Version 2.0                              //
//  2. or (at your option), the GNU Lesser General Public License,  //
//       version 2.1 or greater.                                    //
//                                                                  //
//  Full licence texts are included in the LICENCE file with        //
//  this library.                                                   //
//                                                                  //
//  Author contacts: P.H.Welch@kent.ac.uk K.Chalmers@napier.ac.uk   //
//                                                                  //
//////////////////////////////////////////////////////////////////////

package jcspDemos.altingBarriers;

import jcsp.userIO.*;
import java.awt.Color;

import jcsp.lang.*;
import jcsp.plugNplay.*;
import jcsp.util.*;

public class AltingBarrierGadget1Demo0 {

  public static void main (String[] argv) {

    // final int nUnits = 30, span = 6;
    //
    // final int offInterval = 800, standbyInterval = 1000;
    // milliseconds
    //
    // final int playInterval = 10000, flashInterval = 500;
    // milliseconds

    final int nUnits = Ask.Int ("\nnUnits = ", 3, 30);
    
    final int span = Ask.Int ("span = ", 2, nUnits);

    final int offInterval =
      Ask.Int ("off interval (millisecs) = ", 100, 10000);
    final int standbyInterval =
      Ask.Int ("standby interval (millisecs) = ", 100, 20000);
      
    final int playInterval =
      Ask.Int ("play interval (millisecs) = ", 1000, 1000000000);
    final int flashInterval =
      Ask.Int ("flash interval (millisecs) = ", 50, 1000);

    final Color offColour = Color.black, standbyColour = Color.lightGray;
    
    // make the buttons
    
    final One2OneChannel[] click =
      Channel.one2oneArray (nUnits, new OverWriteOldestBuffer (1));

    final One2OneChannel[] configure = Channel.one2oneArray (nUnits);

    final boolean horizontal = true;

    final FramedButtonArray buttons =
      new FramedButtonArray (
        "AltingBarrier: Gadget 1, Demo 0", nUnits, 100, nUnits*50,
         horizontal, Channel.getInputArray(configure), Channel.getOutputArray(click)
      );

    // construct nUnits barriers, each with span front-ends ...
    
    AltingBarrier[][] ab = new AltingBarrier[nUnits][];
    for (int i = 0; i < nUnits; i++) {
      ab[i] = AltingBarrier.create (span);
    }

    // re-arrange front-ends, ready for distribution to processes ...
    
    AltingBarrier[][]barrier = new AltingBarrier[nUnits][span];
    for (int i = 0; i < nUnits; i++) {
      for (int j = 0; j < span; j++) {
        barrier[i][j] = ab[(i + j) % nUnits][j];
      }
    }

    // make the track and the gadgets

    One2OneChannel[] track = Channel.one2oneArray (nUnits);

    AltingBarrierGadget1[] gadgets = new AltingBarrierGadget1[nUnits];
    for (int i = 0; i < nUnits; i++) {
      gadgets[i] =
        new AltingBarrierGadget1 (
	  barrier[i],
	  track[(i + 1)%nUnits].in(), track[i].out(),
	  click[i].in(), configure[i].out(),
	  offColour, standbyColour,
          offInterval, standbyInterval,
	  playInterval, flashInterval
	);
    }

    // run everything

    new Parallel (
      new CSProcess[] {
        buttons, new Parallel (gadgets)
      }
    ).run ();

  }

}
