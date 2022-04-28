

import chisel3._;

class Datapath extends Module {
    val fetch = Module( new Fetch )
    val decode = Module( new Decode )
    val execute = Module( new Execute )
    val memory = Module( new Mem )
    val writeback = Module( new Writeback )

    val io = IO( new Bundle {
        val switches1 = Input(UInt(8.W)) // Input 1
        val switches2 = Input(UInt(8.W)) // Input 2

        val instr     = Output(UInt(32.W))

        // Input 1
        val disp0     = Output(UInt(6.W))
        val disp1     = Output(UInt(6.W))

        // Input 2
        val disp2     = Output(UInt(6.W))
        val disp3     = Output(UInt(6.W))

        // Output
        val disp4     = Output(UInt(6.W))
        val disp5     = Output(UInt(6.W))
        val disp6     = Output(UInt(6.W))
        val disp7     = Output(UInt(6.W))
    } )

    val input1 = io.switches1
    val input2 = io.switches2
    val output = WireDefault(0.U(32.W))
    output := execute.exec_io.AluRes

    io.instr := fetch.io.Instr;

    /** FETCH **/
    fetch.io.Stall := decode.dec_io.BrEn // stall for 2 CC if branching occurs (1 + reg update)
    fetch.io.BrEn := memory.mem_io.BrEnOut
    fetch.io.BranchAddr := memory.mem_io.BrAddrOut

    /** DECODE **/
    decode.dec_io.Instr := RegNext( fetch.io.Instr )
    decode.dec_io.NextPCIn := RegNext( fetch.io.NextPC )
    decode.dec_io.WriteEnIn := writeback.wb_io.WbEnOut // no RegNext
    decode.dec_io.WriteAddrIn := writeback.wb_io.WriteRegAddrOut // no RegNext
    decode.dec_io.WriteDataIn := writeback.wb_io.WriteDataOut // no RegNext

    /** EXECUTE **/
    execute.exec_io.rs := RegNext(decode.dec_io.rs)
    execute.exec_io.rt := RegNext(decode.dec_io.rt)
    execute.exec_io.rd := RegNext(decode.dec_io.rd)
    execute.exec_io.DataRead1 := RegNext(decode.dec_io.DataRead1)
    execute.exec_io.DataRead2 := RegNext(decode.dec_io.DataRead2)
    execute.exec_io.MemWbEn := memory.mem_io.WbEnOut // no RegNext
    execute.exec_io.MemAddr := memory.mem_io.WriteRegAddrOut // no RegNext
    execute.exec_io.MemVal := RegNext(execute.exec_io.AluRes)
    // Mux(memory.mem_io.WbEnOut === 1.U, memory.mem_io.ReadData, memory.mem_io.AddrIn) // no RegNext - addr mem is alu_res
    execute.exec_io.WbWbEn := writeback.wb_io.WbEnOut // no RegNext
    execute.exec_io.WbAddr := writeback.wb_io.WriteRegAddrOut // no RegNext
    execute.exec_io.WbVal := writeback.wb_io.WriteDataOut // no RegNext
    execute.exec_io.AluOp := RegNext(decode.dec_io.AluOp)
    execute.exec_io.Imm := RegNext(decode.dec_io.Imm)
    execute.exec_io.ImmEn := RegNext(decode.dec_io.ImmEn)
    execute.exec_io.BrEnIn := RegNext(decode.dec_io.BrEn)
    execute.exec_io.NextPC := RegNext(decode.dec_io.NextPCOut)
    execute.exec_io.ReadEnIn := RegNext(decode.dec_io.ReadEn)
    execute.exec_io.WriteEnIn := RegNext(decode.dec_io.WriteEnOut)
    execute.exec_io.WbTypeIn := RegNext(decode.dec_io.WbType)
    execute.exec_io.WbEnIn := RegNext(decode.dec_io.WbEn)

    /** MEMORY **/
    memory.mem_io.WriteEn := RegNext(execute.exec_io.WriteEnOut)
    memory.mem_io.ReadEn := RegNext(execute.exec_io.ReadEnOut)
    memory.mem_io.WbEnIn := RegNext(execute.exec_io.WbEnOut)
    memory.mem_io.WbTypeIn := RegNext(execute.exec_io.WbTypeOut)
    memory.mem_io.WriteData := RegNext(execute.exec_io.WriteData)
    memory.mem_io.AddrIn := RegNext(execute.exec_io.AluRes)
    memory.mem_io.CtrlBrEn := RegNext(execute.exec_io.BrEnOut)
    memory.mem_io.AluBrEn := RegNext(execute.exec_io.zero)
    memory.mem_io.BrAddrIn := RegNext(execute.exec_io.BranchAddrOut)
    memory.mem_io.WriteRegAddrIn := RegNext(execute.exec_io.WriteRegAddr)

    /** WRITEBACK **/
    writeback.wb_io.WbEnIn := RegNext(memory.mem_io.WbEnOut)
    writeback.wb_io.WbTypeIn := RegNext(memory.mem_io.WbTypeOut)
    writeback.wb_io.ReadData := memory.mem_io.ReadData // already one clock delay from the RAM
    writeback.wb_io.AddrData := RegNext(memory.mem_io.AddrOut)
    writeback.wb_io.WriteRegAddrIn := RegNext(memory.mem_io.WriteRegAddrOut)

    /** 7-Seg Displays **/
    val U_decoder7seg_0 = Module( new decoder7seg )
    val U_decoder7seg_1 = Module( new decoder7seg )
    val U_decoder7seg_2 = Module( new decoder7seg )
    val U_decoder7seg_3 = Module( new decoder7seg )
    val U_decoder7seg_4 = Module( new decoder7seg )
    val U_decoder7seg_5 = Module( new decoder7seg )
    val U_decoder7seg_6 = Module( new decoder7seg )
    val U_decoder7seg_7 = Module( new decoder7seg )

    U_decoder7seg_0.io.in := input1(3,0)
    U_decoder7seg_1.io.in := input1(7,4)
    U_decoder7seg_2.io.in := input2(3,0)
    U_decoder7seg_3.io.in := input2(7,4)

    U_decoder7seg_4.io.in := output(3,0)
    U_decoder7seg_5.io.in := output(7,4)
    U_decoder7seg_6.io.in := output(11,8)
    U_decoder7seg_7.io.in := output(15,12)

    io.disp0 := U_decoder7seg_0.io.out // Input1 -> Switches(3:0)
    io.disp1 := U_decoder7seg_1.io.out // Input1 -> Switches(7:4)
    io.disp2 := U_decoder7seg_2.io.out // Input1 -> Switches(3:0)
    io.disp3 := U_decoder7seg_3.io.out // Input1 -> Switches(7:4)
    io.disp4 := U_decoder7seg_4.io.out // Output -> Output(3:0)
    io.disp5 := U_decoder7seg_5.io.out // Output -> Output(7:4)
    io.disp6 := U_decoder7seg_6.io.out // Output -> Output(11:8)
    io.disp7 := U_decoder7seg_7.io.out // Output -> Output(15:12)
}

object Datapath extends App {
    (new chisel3.stage.ChiselStage).emitVerilog(new Datapath, Array("--target-dir", "generated_verilog"))
}
