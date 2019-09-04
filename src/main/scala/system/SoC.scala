package system

import noop.{NOOP, NOOPConfig}
import bus.axi4.{AXI4, AXI4Lite}
import bus.simplebus._

import chisel3._

class NOOPSoC(implicit val p: NOOPConfig) extends Module {
  val io = IO(new Bundle{
    val mem = new AXI4
    val mmio = (if (p.FPGAPlatform) { new AXI4Lite } else { new SimpleBusUL })
  })

  val noop = Module(new NOOP)
  val cohMg = Module(new CoherenceInterconnect)
  cohMg.io.in(0) <> noop.io.imem
  cohMg.io.in(1) <> noop.io.dmem
  io.mem <> cohMg.io.out.toAXI4()

  if (p.FPGAPlatform) io.mmio <> noop.io.mmio.toAXI4()
  else io.mmio <> noop.io.mmio
}