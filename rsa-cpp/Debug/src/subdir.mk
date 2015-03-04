################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
C_SRCS += \
../src/RSATestBench.c \
../src/montgomery_array.c \
../src/montgomery_array_test.c 

OBJS += \
./src/RSATestBench.o \
./src/montgomery_array.o \
./src/montgomery_array_test.o 

C_DEPS += \
./src/RSATestBench.d \
./src/montgomery_array.d \
./src/montgomery_array_test.d 


# Each subdirectory must supply rules for building sources it contributes
src/%.o: ../src/%.c
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C Compiler'
	gcc -O0 -g3 -Wall -c -fmessage-length=0 -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


