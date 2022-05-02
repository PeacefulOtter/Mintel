	
import chisel3._
import chisel3.util.BitPat

object Instructions {

    // IMM SEL
    val IMM_N = 0.U(1.W)
    val IMM_Y = 1.U(1.W)
    // BR EN
    val BR_N = 0.U(2.W)
    val BR_Y = 1.U(2.W)
    val BR_J = 2.U(2.W)
    // LD EN
    val LD_N = 0.U(1.W)
    val LD_Y = 1.U(1.W)
    // ST EN
    val ST_N = 0.U(1.W)
    val ST_Y = 1.U(1.W)
    // WB TYPE
    val WB_ALU = 0.U(1.W)
    val WB_MEM = 1.U(1.W)
    // WR EN
    val WB_N = 0.U(1.W)
    val WB_Y = 1.U(1.W)
    // SIGNED
    val SI_N = 0.U(1.W)
    val SI_Y = 1.U(1.W)

    val map = Array(
        //            alu_op    imm_en   br_en    ld_en    st_en   wb_type   wb_en  signed
        NOP   -> List(ALU.add,  IMM_N,   BR_N,    LD_N,    ST_N,   WB_ALU,   WB_N,  SI_N),
        // Arithmetic
        ADD   -> List(ALU.add,  IMM_N,   BR_N,    LD_N,    ST_N,   WB_ALU,   WB_Y,  SI_Y),
        ADDI  -> List(ALU.add,  IMM_Y,   BR_N,    LD_N,    ST_N,   WB_ALU,   WB_Y,  SI_Y),
        ADDU  -> List(ALU.addu, IMM_N,   BR_N,    LD_N,    ST_N,   WB_ALU,   WB_Y,  SI_N),
        ADDIU -> List(ALU.addu, IMM_Y,   BR_N,    LD_N,    ST_N,   WB_ALU,   WB_Y,  SI_N),
        SUB   -> List(ALU.sub,  IMM_N,   BR_N,    LD_N,    ST_N,   WB_ALU,   WB_Y,  SI_Y),
        SUBU  -> List(ALU.subu, IMM_N,   BR_N,    LD_N,    ST_N,   WB_ALU,   WB_Y,  SI_N),
        // Logical
        AND   -> List(ALU.and,  IMM_N,   BR_N,    LD_N,    ST_N,   WB_ALU,   WB_Y,  SI_N),
        ANDI  -> List(ALU.and,  IMM_Y,   BR_N,    LD_N,    ST_N,   WB_ALU,   WB_Y,  SI_N),
        OR    -> List(ALU.or,   IMM_N,   BR_N,    LD_N,    ST_N,   WB_ALU,   WB_Y,  SI_N),
        ORI   -> List(ALU.or,   IMM_Y,   BR_N,    LD_N,    ST_N,   WB_ALU,   WB_Y,  SI_N),
        XOR   -> List(ALU.xor,  IMM_N,   BR_N,    LD_N,    ST_N,   WB_ALU,   WB_Y,  SI_N),
        XORI  -> List(ALU.xor,  IMM_Y,   BR_N,    LD_N,    ST_N,   WB_ALU,   WB_Y,  SI_N),
        // Shifts
        SLL   -> List(ALU.sll,  IMM_Y,   BR_N,    LD_N,    ST_N,   WB_ALU,   WB_Y,  SI_N),
        SRL   -> List(ALU.srl,  IMM_Y,   BR_N,    LD_N,    ST_N,   WB_ALU,   WB_Y,  SI_N),
        SLLV  -> List(ALU.sllv, IMM_N,   BR_N,    LD_N,    ST_N,   WB_ALU,   WB_Y,  SI_N),
        SRLV  -> List(ALU.srlv, IMM_N,   BR_N,    LD_N,    ST_N,   WB_ALU,   WB_Y,  SI_N),
        // Compare
        SLT   -> List(ALU.slt,  IMM_N,   BR_N,    LD_N,    ST_N,   WB_ALU,   WB_Y,  SI_Y),
        SLTI  -> List(ALU.slt,  IMM_Y,   BR_N,    LD_N,    ST_N,   WB_ALU,   WB_Y,  SI_Y),
        SLTU  -> List(ALU.sltu, IMM_N,   BR_N,    LD_N,    ST_N,   WB_ALU,   WB_Y,  SI_N),
        SLTIU -> List(ALU.sltu, IMM_Y,   BR_N,    LD_N,    ST_N,   WB_ALU,   WB_Y,  SI_N),
        // Branch
        BNE   -> List(ALU.ne,   IMM_N,   BR_Y,    LD_N,    ST_N,   WB_ALU,   WB_N,  SI_N),
        BEQ   -> List(ALU.eq,   IMM_N,   BR_Y,    LD_N,    ST_N,   WB_ALU,   WB_N,  SI_N),
        BLT   -> List(ALU.slt,  IMM_N,   BR_Y,    LD_N,    ST_N,   WB_ALU,   WB_N,  SI_N),
        BGE   -> List(ALU.ge,   IMM_N,   BR_Y,    LD_N,    ST_N,   WB_ALU,   WB_N,  SI_N),
        // Load
        LW    -> List(ALU.add,  IMM_Y,   BR_N,    LD_Y,    ST_N,   WB_ALU,   WB_Y,  SI_N),
        // Store
        SW    -> List(ALU.add,  IMM_Y,   BR_N,    LD_N,    ST_Y,   WB_MEM,   WB_N,  SI_N),
        // Jumps
        J     -> List(ALU.eq,   IMM_Y,   BR_J,    LD_N,    ST_N,   WB_ALU,   WB_N,  SI_N)
    )

    val default: List[UInt] = map.apply(0)._2

    def NOP   = BitPat("b00000000000000000000000000000000")  // X
    // Arithmetic
    def ADD   = BitPat("b000000???????????????00000100000")  // X
    def ADDU  = BitPat("b000000???????????????00000100001")
    def ADDI  = BitPat("b001000??????????????????????????")  // X
    def ADDIU = BitPat("b001001??????????????????????????")
    def SUB   = BitPat("b000000???????????????00000100010")  // X
    def SUBU  = BitPat("b000000???????????????00000100011")  // X
    // def MULT  = BitPat("b000000??????????0000000000011000")
    // Logical
    def AND   = BitPat("b000000???????????????00000100100")  // X
    def ANDI  = BitPat("b001100??????????????????????????")  // X
    def OR    = BitPat("b000000???????????????00000100101")  // X
    def ORI   = BitPat("b001101??????????????????????????")  // X
    def XOR   = BitPat("b000000???????????????00000100110")  // X
    def XORI  = BitPat("b001110??????????????????????????")  // X
    // Shift
    def SLL   = BitPat("b00000000000???????????????000000")  // X
    def SRL   = BitPat("b00000000000???????????????000010")  // X
    def SLLV  = BitPat("b000000???????????????00000000100")  // X
    def SRLV  = BitPat("b000000???????????????00000000110")  // X
    // Compare
    def SLT   = BitPat("b000000???????????????00000101010")  // X
    def SLTI  = BitPat("b001010??????????????????????????")  // X
    def SLTU  = BitPat("b000000???????????????00000101011")  // X
    def SLTIU = BitPat("b001011??????????????????????????")  // X
    // Branch
    def BEQ   = BitPat("b000100??????????????????????????")  // X
    def BNE   = BitPat("b000101??????????????????????????")  // X
    def BLT   = BitPat("b000110??????????????????????????")  // X
    def BGE   = BitPat("b000111??????????????????????????")  // X
    // Mem
    def LW    = BitPat("b100011??????????????????????????")
    def SW    = BitPat("b101011??????????????????????????")
    // Jump
    def J     = BitPat("b000010??????????????????????????")
}
