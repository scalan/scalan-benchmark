package benchmark.jni

import java.io.File
import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations._

import scala.util.Random
import scalan.ScalanCommunityDslExp
import scalan.compilation.lms.{ CommunityLmsBackend, CoreBridge }
import scalan.compilation.lms.scalac.CommunityLmsCompilerScala
import scalan.monads.MonadsDslExp
import scalan.primitives.EffectfulCompiler
import scalan.effects.StateExamples
import scalan.util.FileUtil
import scalan.compilation.GraphVizConfig

object ZipWithIndexBenchmark {

  trait CommonData {
    val rnd = new Random(1)

    var arr: Array[Double] = Array.empty

    def genArray(len: Int): Array[Double] = {
      { for (i <- 1 to len) yield { rnd.nextDouble() } }.toArray
    }
  }

  trait ScalaState extends CommonData {

    class ProgExp extends StateExamples with CommunityLmsCompilerScala with CoreBridge
        with ScalanCommunityDslExp with EffectfulCompiler with MonadsDslExp {
      val State = new State0Manager[Int]
      val lms = new CommunityLmsBackend
    }

    val ctx = new ProgExp
    val baseDir = FileUtil.file("ZipWithIndex")
    protected implicit val cfg = ctx.defaultCompilerConfig.copy(scalaVersion = Some("2.11.2"))

    protected def loadMethod[A, B](prog: ProgExp)(baseDir: File, functionName: String, f: prog.Exp[A => B])(implicit compilerConfig: prog.CompilerConfig) =
      {
        val funcDir = FileUtil.file(baseDir, functionName)

        val compilerOutput = prog.buildExecutable(funcDir, functionName, f, GraphVizConfig.none)
        val (cls, method) = prog.loadMethod(compilerOutput)
        val instance = cls.newInstance()
        (method.invoke(instance, _: AnyRef)).asInstanceOf[A => B]
      }

  }

  trait CxxState extends CommonData {
    val nm = new NativeMethods
  }

  @State(Scope.Benchmark)
  class CommonState extends CommonData {

    @Param(Array("false"))
    var doCheck: Boolean = false;

    @Param(Array("1000", "3000", "10000"))
    var len: Int = 0

    var res: Array[(Int, Double)] = Array.empty
    var correct_res: Array[(Int, Double)] = Array.empty

    @Setup
    def prepare(): Unit = {
      arr = genArray(len)
      correct_res = { (0 until arr.length).zip(arr) }.toArray
    }

    @TearDown
    def check(): Unit = {
      if (doCheck) {
        println(s"${arr.mkString(",")}")
        println(s"${res.mkString(",")}")
        println(s"${correct_res.mkString(",")}")
        assert(res.sameElements(correct_res))
      }
    }
  }

  @State(Scope.Benchmark)
  class StateScalaZipArrayWithIndex extends CommonState with ScalaState {
    val fu = loadMethod(ctx)(baseDir, "zipWithArrayIndex", ctx.zipArrayWithIndexW)
  }

  @State(Scope.Benchmark)
  class StateCxxZipArrayWithIndex extends CommonState with CxxState {
    val fu = nm.jniZipArrayWithIndex(_)
  }

  @State(Scope.Benchmark)
  class StateScalaZipCollectionWithIndex extends CommonState with ScalaState {
    val fu = loadMethod(ctx)(baseDir, "zipCollectionArrayIndex", ctx.zipCollectionWithIndexW)
  }

  @State(Scope.Benchmark)
  class StateCxxZipCollectionWithIndex extends CommonState with CxxState {
    val fu = nm.jniZipCollectionWithIndex(_)
  }

  @State(Scope.Benchmark)
  class StateScalaZipCollectionWithIndex2 extends CommonState with ScalaState {
    val fu = loadMethod(ctx)(baseDir, "zipCollectionArrayIndex2", ctx.zipCollectionWithIndexW2)
  }

  @State(Scope.Benchmark)
  class StateCxxZipCollectionWithIndex2 extends CommonState with CxxState {
    val fu = nm.jniZipCollectionWithIndex2(_)
  }

  @State(Scope.Benchmark)
  class StateScalaZipCollectionWithIndex3 extends CommonState with ScalaState {
    val fu = loadMethod(ctx)(baseDir, "zipCollectionArrayIndex3", ctx.zipCollectionWithIndexW3)
  }

  @State(Scope.Benchmark)
  class StateCxxZipCollectionWithIndex3 extends CommonState with CxxState {
    val fu = nm.jniZipCollectionWithIndex3(_)
  }
}

@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(value = 1)
@Warmup(iterations = 10)
@Measurement(iterations = 10)
class ZipWithIndexBenchmark {
  System.loadLibrary("jniZipWithIndex")

  import benchmark.jni.ZipWithIndexBenchmark._

  @Benchmark
  def zipArrayWithIndex_scala(c: StateScalaZipArrayWithIndex): Array[(Int, Double)] = {
    c.res = c.fu(c.arr)
    c.res
  }

  @Benchmark
  def zipArrayWithIndex_cxx(c: StateCxxZipArrayWithIndex): Array[(Int, Double)] = {
    c.res = c.fu(c.arr)
    c.res
  }

  @Benchmark
  def zipCollectionWithIndex_scala(c: StateScalaZipCollectionWithIndex): Array[(Int, Double)] = {
    c.res = c.fu(c.arr)
    c.res
  }

  @Benchmark
  def zipCollectionWithIndex_cxx(c: StateCxxZipCollectionWithIndex): Array[(Int, Double)] = {
    c.res = c.fu(c.arr)
    c.res
  }

  @Benchmark
  def zipCollectionWithIndex2_scala(c: StateScalaZipCollectionWithIndex2): Array[(Int, Double)] = {
    c.res = c.fu(c.arr)
    c.res
  }

  @Benchmark
  def zipCollectionWithIndex2_cxx(c: StateCxxZipCollectionWithIndex2): Array[(Int, Double)] = {
    c.res = c.fu(c.arr)
    c.res
  }

  @Benchmark
  def zipCollectionWithIndex3_scala(c: StateScalaZipCollectionWithIndex3): Array[(Int, Double)] = {
    c.res = c.fu(c.arr)
    c.res
  }

  @Benchmark
  def zipCollectionWithIndex3_cxx(c: StateCxxZipCollectionWithIndex3): Array[(Int, Double)] = {
    c.res = c.fu(c.arr)
    c.res
  }
}
